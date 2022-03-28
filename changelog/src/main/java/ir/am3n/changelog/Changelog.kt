package ir.am3n.changelog

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.XmlResourceParser
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ir.am3n.changelog.Utils.getAppVersion
import ir.am3n.needtool.screenHeight
import kotlinx.android.synthetic.main.changelog.*
import org.xmlpull.v1.XmlPullParser
import java.sql.Date
import java.text.ParseException
import kotlin.math.roundToInt

class Changelog : DialogFragment() {


    companion object {

        const val TAG = "Changelog"

        private const val LAST_VERSION_CODE = "LAST_VERSION_CODE"
        private const val LAST_VERSION_NAME = "LAST_VERSION_NAME"
        private fun sh(context: Context?): SharedPreferences? {
            return context?.getSharedPreferences("Changelog", Context.MODE_PRIVATE)
        }

        /** Use this value if you want all the changelog (i.e. all the release entries) to appear. */
        const val ALL_VERSIONS = 0
        const val NEW_VERSIONS = -1

        /**
         * Create a dialog displaying the changelog.
         * @param activity The calling activity
         * @param presentFrom Define the oldest version to show. In other words, the dialog will contains
         * release entries with a `versionCode` attribute >= [presentFrom]. Default to all.
         * @param title The title of the dialog. Default to "Changelog"
         * @param changelogId The resourceId of the xml file, default to `R.xml.changelog`
         */
        fun present(
            activity: AppCompatActivity,
            presentMode: PresentMode = PresentMode.DEBUG,
            presentFrom: Int = ALL_VERSIONS,
            ignoreAlphaBeta: Boolean = true,
            title: String? = null,
            buttonText: String? = null,
            changelogId: Int? = null,
            onDismissOrIgnoredListener: (() -> Unit)? = {}
        ) {

            val sh = sh(activity)

            val changelog = Changelog().apply {
                lastVersionCode = sh?.getLong(LAST_VERSION_CODE, 0) ?: 0
                this.presentMode = presentMode
                this.presentFrom = presentFrom
                this.title = title
                this.buttonText = buttonText
                this.changelogId = changelogId
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
                                    ?.putString(LAST_VERSION_NAME, "$nowNumOfVersionNamePart1.$nowNumOfVersionNamePart2.$nowNumOfVersionNamePart3")
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
    private var title: String? = null
    private var buttonText: String? = null
    private var changelogId: Int? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.changelog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTitle?.text = title.toString()
        if (buttonText != null) {
            btnContinue?.isVisible = true
            btnContinue?.text = buttonText
        } else {
            btnContinue?.isVisible = false
        }

        changelogId?.let {
            val changelog = loadChangelog(it, presentFrom)
            rcl?.adapter = ChangelogAdapter(changelog)
        }

        btnContinue?.setOnClickListener {
            try {
                dialog?.dismiss()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

        view.doOnPreDraw {
            try {
                val ratio = 4f / 5f
                val availHeight = (requireContext().screenHeight - txtTitle.measuredHeight - btnContinue.measuredHeight) * ratio
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


    /**
     * Read the changelog.xml and create a list of [ChangelogItem] and [ChangelogHeader].
     * @param resourceId: the name of the changelog file, default to R.xml.changelog
     * @param version: the lowest release to display
     * @return the list of [ChangelogItem], in the order of the [resourceId] file (most to less recent)
     */
    private fun loadChangelog(resourceId: Int, version: Int = ALL_VERSIONS): MutableList<ChangelogItem> {
        val changelogItems = mutableListOf<ChangelogItem>()
        try {
            context?.resources?.getXml(resourceId)?.let { xml ->
                while (xml.eventType != XmlPullParser.END_DOCUMENT) {
                    if (xml.eventType == XmlPullParser.START_TAG && xml.name == XmlTags.RELEASE) {
                        val releaseVersion = Integer.parseInt(xml.getAttributeValue(null,
                            XmlTags.VERSION_CODE
                        ))
                        if (releaseVersion <= lastVersionCode && version == -1)
                            break
                        changelogItems.addAll(parseReleaseTag(requireContext(), xml))
                        if (releaseVersion <= version && version >= 0)
                            break
                    } else {
                        xml.next()
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return changelogItems
    }

    /**
     * Parse one release tag attribute.
     * @param context the calling activity
     * @param xml the xml resource parser. Its cursor should be at a release tag.
     * @return a list containing one [ChangelogHeader] and zero or more [ChangelogItem]
     */
    private fun parseReleaseTag(context: Context, xml: XmlResourceParser): MutableList<ChangelogItem> {
        require(xml.name == XmlTags.RELEASE && xml.eventType == XmlPullParser.START_TAG)
        val items = mutableListOf<ChangelogItem>()
        // parse header
        items.add(
            ChangelogHeader(
                version = xml.getAttributeValue(null, XmlTags.VERSION_NAME) ?: "X.X",
                date = xml.getAttributeValue(null, XmlTags.DATE)?.let { parseDate(context, it) },
                summary = xml.getAttributeValue(null, XmlTags.SUMMARY)
            )
        )
        xml.next()
        // parse changes
        while (xml.name == XmlTags.ITEM || xml.eventType == XmlPullParser.TEXT) {
            if (xml.eventType == XmlPullParser.TEXT) {
                items.add(ChangelogItem(xml.text))
            }
            xml.next()
        }
        return items
    }

    /**
     * Format a date string.
     * @param context The calling activity
     * @param dateString The date string, in ISO format (YYYY-MM-dd)
     * @return The date formatted using the system locale, or [dateString] if the parsing failed.
     */
    private fun parseDate(context: Context, dateString: String): String {
        return try {
            val parsedDate = Date.valueOf(dateString)
            DateFormat.getDateFormat(context).format(parsedDate)
        } catch (_: ParseException) {
            // wrong date format... Just keep the string as is
            dateString
        }
    }

}
