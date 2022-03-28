package ir.am3n.changelog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/** An adapter for [ChangelogItem] and [ChangelogHeader]. Nothing special, except that
 * it will create two types of holders, depending on the data type. */
class ChangelogAdapter(private val list: List<ChangelogItem>) : RecyclerView.Adapter<ChangelogHolder>() {

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int =
            if (list[position] is ChangelogHeader) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangelogHolder = when {
        viewType > 0 -> ChangelogHeaderHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell_header, parent, false))
        else -> ChangelogHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell, parent, false))
    }

    override fun onBindViewHolder(holder: ChangelogHolder, position: Int) {

        val item = list[position]

        if (holder is ChangelogHeaderHolder) {
            val header = item as ChangelogHeader
            holder.txtVersion.text = header.version
            holder.txtSummary.text = header.summary
            holder.txtSummary.visibility = if (header.summary != null) View.VISIBLE else View.GONE
            holder.txtDate.text = header.date
        } else {
            holder.txtDesc?.text = item.description
        }

    }
}