package ir.am3n.changelog

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.am3n.changelog.R

/** Base ChangelogHolder for the [ChangelogAdapter]. Used for [ChangelogItem]. */
open class ChangelogHolder(v: View) : RecyclerView.ViewHolder(v) {
    val txtDesc: TextView? = v.findViewById(R.id.textDesc)
}