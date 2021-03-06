package ir.am3n.changelog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ir.am3n.changelog.Utils.getAppVersion
import ir.am3n.changelog.Utils.loadChangelog
import ir.am3n.needtool.screenHeight
import kotlinx.android.synthetic.main.changelog.*
import kotlin.math.roundToInt

class Changelog : DialogFragment() {


    companion object {

        private const val LAST_VERSION_CODE = "LAST_VERSION_CODE"
        private const val LAST_VERSION_NAME = "LAST_VERSION_NAME"
        private fun sh(context: Context?): SharedPreferences? {
            return context?.getSharedPreferences("Changelog", Context.MODE_PRIVATE)
        }

        const val TAG = "Changelog"

        /** Use this value if you want all the changelog (i.e. all the release entries) to appear. */
        const val ALL_VERSIONS = 0
        const val NEW_VERSIONS = -1

        /** Rounded drawable background, if you want to use this, you should override `?attr/colorOnPrimary` */
        val DEFAULT_BACKGROUND = R.drawable.bg_changelog_dialog

        /**
         * Create a dialog displaying the changelog.
         * @param activity The calling activity.
         * @param presentMode see [PresentMode]
         * @param presentFrom Define the oldest version to show. In other words, the dialog will contains
         * release entries with a `versionCode` attribute >= [presentFrom]. Default to all.
         * @param ignoreAlphaBeta Ignore displaying changelogs if `versionName` contains 'alpha' or 'beta'.
         * @param background The background of the dialog. You can use in-library drawable [DEFAULT_BACKGROUND]
         * @param title The title holder of the dialog. Default to null means title visibility is gone.
         * @param button The button holder of the dialog. Default to null means button visibility is gone.
         * @param defaultFont The typeface that set to all texts.
         * @param changelogId The resourceId of the xml file.
         * @param layoutDirection Change direction to support rtl or by locale. Default to null.
         * @param onDismissOrIgnoredListener Changelog `onDismiss` or `onIgnore` callback.
         */
        @SuppressLint("ApplySharedPref")
        fun present(
            activity: AppCompatActivity,
            presentMode: PresentMode = PresentMode.DEBUG,
            presentFrom: Int = ALL_VERSIONS,
            ignoreAlphaBeta: Boolean = true,
            @DrawableRes background: Int? = null,
            title: Holder? = null,
            button: Holder? = null,
            defaultFont: Typeface? = null,
            changelogId: Int,
            layoutDirection: Int? = null,
            onDismissOrIgnoredListener: (() -> Unit)? = {}
        ) {

            val sh = sh(activity)

            val changelog = Changelog().apply {
                lastVersionCode = try {
                    sh?.getLong(LAST_VERSION_CODE, 0) ?: 0
                } catch (t: Throwable) {
                    (sh?.getInt(LAST_VERSION_CODE, 0) ?: 0).toLong().apply {
                        sh?.edit()?.putLong(LAST_VERSION_CODE, this)?.commit()
                    }
                }
                this.presentMode = presentMode
                this.presentFrom = presentFrom
                this.background = background
                this.title = title
                this.button = button
                this.defaultFont = defaultFont
                this.changelogId = changelogId
                this.layoutDirection = layoutDirection
                this.onDismissOrIgnoredListener = onDismissOrIgnoredListener
            }

            when (presentMode) {
                PresentMode.DEBUG -> {
                    changelog.show(activity.supportFragmentManager, TAG)
                    return
                }
                PresentMode.NEVER -> {
                    onDismissOrIgnoredListener?.invoke()
                    return
                }
                else -> {
                    val lastVersionCode = sh?.getLong(LAST_VERSION_CODE, 0) ?: 0
                    val lastVersionName = sh?.getString(LAST_VERSION_NAME, "") ?: ""

                    var lastNumOfVersionNamePart1 = 0
                    var lastNumOfVersionNamePart2 = 0
                    var lastNumOfVersionNamePart3 = 0
                    var nowNumOfVersionNamePart1 = 0
                    var nowNumOfVersionNamePart2 = 0
                    var nowNumOfVersionNamePart3 = 0

                    try {

                        val nowVersionName: String = activity.getAppVersion().first
                        val nowVersionCode: Long = activity.getAppVersion().second

                        if (ignoreAlphaBeta && nowVersionName.contains("(alpha|beta)".toRegex())) {
                            onDismissOrIgnoredListener?.invoke()
                            return
                        }

                        // Obtain the first two numbers of current version name.
                        nowVersionName.split("\\.".toRegex())
                            .filter { it.isNotEmpty() && it.isNotBlank() }
                            .apply {
                                if (size >= 1) {
                                    nowNumOfVersionNamePart1 = this[0].toInt()
                                }
                                if (size >= 2) {
                                    nowNumOfVersionNamePart2 = this[1].toInt()
                                }
                                if (size >= 3) {
                                    nowNumOfVersionNamePart3 = this[2].toInt()
                                }
                            }

                        // Obtain the first two numbers of last version name.
                        lastVersionName.split("\\.".toRegex())
                            .filter { it.isNotEmpty() && it.isNotBlank() }
                            .apply {
                                if (size >= 1) {
                                    lastNumOfVersionNamePart1 = this[0].toInt()
                                }
                                if (size >= 2) {
                                    lastNumOfVersionNamePart2 = this[1].toInt()
                                }
                                if (size >= 3) {
                                    lastNumOfVersionNamePart3 = this[2].toInt()
                                }
                            }

                        if (presentMode == PresentMode.ALWAYS) {
                            if (nowVersionCode >= 0 && nowVersionCode > lastVersionCode) {
                                changelog.show(activity.supportFragmentManager, TAG)
                                sh?.edit()?.putLong(LAST_VERSION_CODE, nowVersionCode)?.apply()
                                return
                            }
                        } else {
                            // PresentMode.IF_NEEDED
                            var present = false
                            if (nowNumOfVersionNamePart1 >= 0 && nowNumOfVersionNamePart1 > lastNumOfVersionNamePart1) {
                                present = true
                            } else if (nowNumOfVersionNamePart1 == lastNumOfVersionNamePart1) {
                                if (nowNumOfVersionNamePart2 >= 0 && nowNumOfVersionNamePart2 > lastNumOfVersionNamePart2) {
                                    present = true
                                } else if (nowNumOfVersionNamePart2 == lastNumOfVersionNamePart2) {
                                    if (nowNumOfVersionNamePart3 >= 0 && nowNumOfVersionNamePart3 > lastNumOfVersionNamePart3) {
                                        present = true
                                    }
                                }
                            }
                            if (nowVersionCode < 0 || lastVersionCode < 0 || nowVersionCode < lastVersionCode) {
                                present = false
                            }

                            if (present) {
                                changelog.show(activity.supportFragmentManager, TAG)
                                sh?.edit()
                                    ?.putLong(LAST_VERSION_CODE, nowVersionCode)
                                    ?.putString(
                                        LAST_VERSION_NAME,
                                        "$nowNumOfVersionNamePart1.$nowNumOfVersionNamePart2.$nowNumOfVersionNamePart3"
                                    )
                                    ?.apply()
                                return
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            onDismissOrIgnoredListener?.invoke()

            return
        }

        /**
         * Clear library caches
         */
        fun clear(context: Context?) {
            sh(context)?.edit()?.clear()?.apply()
        }

    }


    private var lastVersionCode = 0L
    private var presentMode: PresentMode = PresentMode.DEBUG
    private var presentFrom: Int = ALL_VERSIONS
    private var background: Int? = null
    private var title: Holder? = null
    private var button: Holder? = null
    private var defaultFont: Typeface? = null
    private var changelogId: Int? = null
    private var layoutDirection: Int? = null
    private var onDismissOrIgnoredListener: (() -> Unit)? = {}

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        if (background != null) {
            dialog?.window?.setBackgroundDrawableResource(background!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                dialog?.window?.setClipToOutline(true)
        }
        return inflater.inflate(R.layout.changelog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (layoutDirection != null)
            changelog.direction = layoutDirection


        if (title != null) {
            txtTitle.isVisible = true
            defaultFont?.let { txtTitle.typeface = it }
            title?.text?.let { txtTitle.text = it }
            title?.font?.let { txtTitle.typeface = it }
            title?.color?.let { txtTitle.setTextColor(it) }
        } else {
            txtTitle.isVisible = false
        }


        val logs = loadChangelog(context, changelogId, lastVersionCode, presentFrom)
        if (logs.isNullOrEmpty())
            return dismissAllowingStateLoss()
        rcl.adapter = ChangelogAdapter(logs, defaultFont, layoutDirection)


        if (button != null) {
            btnContinue.isVisible = true
            defaultFont?.let { btnContinue.typeface = it }
            button?.text?.let { btnContinue.text = it }
            button?.font?.let { btnContinue.typeface = it }
            button?.color?.let { btnContinue.setTextColor(it) }
            btnContinue.setOnClickListener {
                try {
                    dismissAllowingStateLoss()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        } else {
            btnContinue.isVisible = false
        }


        view.doOnPreDraw {
            try {
                val ratio = 4f / 5f
                val availHeight =
                    (requireContext().screenHeight - txtTitle.measuredHeight - btnContinue.measuredHeight) * ratio
                rlvRcl?.setMaxHeightPx(availHeight.roundToInt())
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissOrIgnoredListener?.invoke()
    }

}
