package ir.am3n.changelog.sample

import android.os.Bundle
import android.util.LayoutDirection
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.am3n.changelog.Changelog
import ir.am3n.changelog.Holder
import ir.am3n.changelog.PresentMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRestart.setOnClickListener {
            recreate()
        }

        Changelog.present(
            activity = this,
            presentMode = PresentMode.IF_NEEDED,
            presentFrom = Changelog.NEW_VERSIONS,
            ignoreAlphaBeta = false,
            title = Holder(
                text = getString(R.string.whats_new),
                font = ResourcesCompat.getFont(applicationContext, R.font.font_thin)
            ),
            button = Holder(
                text = getString(R.string._continue),
                color = ContextCompat.getColor(applicationContext, R.color.teal_700)
            ),
            defaultFont = ResourcesCompat.getFont(applicationContext, R.font.font_regular),
            changelogId = R.xml.changelog,
            //layoutDirection = LayoutDirection.LOCALE, /* instead of `layoutDirection` use `android:supportsRtl="true"` */
            onDismissOrIgnoredListener = {
                Toast.makeText(this, "onDismissOrIgnored", Toast.LENGTH_SHORT).show()
            }
        )

        Changelog.clear(applicationContext)

    }

}