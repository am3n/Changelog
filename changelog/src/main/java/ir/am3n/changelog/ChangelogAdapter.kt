package ir.am3n.changelog

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import ir.am3n.needtool.iDp2Px

/** An adapter for [ChangelogItem] and [ChangelogHeader]. Nothing special, except that
 * it will create two types of holders, depending on the data type. */
class ChangelogAdapter(
    private val list: List<ChangelogItem>,
    private val defaultFont: Typeface?,
    private val layoutDirection: Int?
) : RecyclerView.Adapter<ChangelogHolder>() {

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int =
        if (list[position] is ChangelogHeader) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangelogHolder = when {
        viewType > 0 -> ChangelogHeaderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell_header, parent, false)
        )
        else -> ChangelogHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChangelogHolder, position: Int) {

        val item = list[position]

        if (holder is ChangelogHeaderHolder) {
            val header = item as ChangelogHeader

            layoutDirection?.let { holder.cnsl.direction = it }

            holder.txtVersion.text = header.version
            holder.txtVersion.typeface = defaultFont

            holder.txtSummary.visibility = if (header.summary != null) View.VISIBLE else View.GONE
            layoutDirection?.let { holder.txtSummary.direction = it }
            holder.txtSummary.text = header.summary
            holder.txtSummary.typeface = defaultFont

            holder.txtDate.text = header.date
            holder.txtDate.typeface = defaultFont

        } else {

            layoutDirection?.let { holder.linr.direction = it }

            layoutDirection?.let { holder.imgDesc.direction = it }

            holder.imgDesc.setImageResource(when (item.type) {
                XmlTags.ItemType.NEW -> R.drawable.ic_changelog_item_new
                XmlTags.ItemType.CHANGE -> R.drawable.ic_changelog_item_change
                XmlTags.ItemType.FIX -> R.drawable.ic_changelog_item_fix
                XmlTags.ItemType.INFO -> R.drawable.ic_changelog_item_info
                XmlTags.ItemType.CUSTOM -> item.icon ?: R.drawable.ic_changelog_item_custom_default
                XmlTags.ItemType.TEXT -> {
                    holder.imgDesc.updateLayoutParams<LinearLayout.LayoutParams> {
                        width = 0
                        updateMarginsRelative(start = 6.iDp2Px)
                    }
                    0
                }
                else -> 0
            })
            holder.txtDesc.text = item.description
            holder.txtDesc.typeface = defaultFont
        }

    }
}