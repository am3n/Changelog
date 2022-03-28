package ir.am3n.changelog

import android.view.View
import android.widget.TextView
import ir.am3n.changelog.R

/** ChangelogHolder for the [ChangelogAdapter]. Used for [ChangelogHeader]. */
class ChangelogHeaderHolder(v: View) : ChangelogHolder(v) {
    val txtVersion: TextView = v.findViewById(R.id.txtVersion)
    val txtSummary: TextView = v.findViewById(R.id.txtSummary)
    val txtDate: TextView = v.findViewById(R.id.txtDate)
}