package ir.am3n.changelog

import android.view.View
import android.widget.TextView
import ir.am3n.needtool.views.A3ConstraintLayout
import ir.am3n.needtool.views.A3TextView

/** ChangelogHolder for the [ChangelogAdapter]. Used for [ChangelogHeader]. */
class ChangelogHeaderHolder(v: View) : ChangelogHolder(v) {
    val cnsl: A3ConstraintLayout = v.findViewById(R.id.cellHeader)
    val txtVersion: TextView = v.findViewById(R.id.txtVersion)
    val txtSummary: A3TextView = v.findViewById(R.id.txtSummary)
    val txtDate: TextView = v.findViewById(R.id.txtDate)
}