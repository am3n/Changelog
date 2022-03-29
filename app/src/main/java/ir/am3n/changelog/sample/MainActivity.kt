package ir.am3n.changelog.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.am3n.changelog.Changelog
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
            title = getString(R.string.whats_new),
            buttonText = "Ok!",
            changelogId = R.xml.changelog,
            //layoutDirection = LayoutDirection.RTL,
            onDismissOrIgnoredListener = {
                Toast.makeText(this, "onDismissOrIgnored", Toast.LENGTH_SHORT).show()
            }
        )

        Changelog.clear(applicationContext)

    }

}