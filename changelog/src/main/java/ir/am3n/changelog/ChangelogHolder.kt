package ir.am3n.changelog

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.am3n.needtool.views.A3ImageView
import ir.am3n.needtool.views.A3LinearLayout

/** Base ChangelogHolder for the [ChangelogAdapter]. Used for [ChangelogItem]. */
open class ChangelogHolder(v: View) : RecyclerView.ViewHolder(v) {
    val linr: A3LinearLayout get() = itemView.findViewById(R.id.cell)
    val imgDesc: A3ImageView get() = itemView.findViewById(R.id.imgDesc)
    val txtDesc: TextView get() = itemView.findViewById(R.id.textDesc)
}