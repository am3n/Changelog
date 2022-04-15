package ir.am3n.changelog

/** Holds information about one "item", i.e. a <change> */
open class ChangelogItem(
    val type: XmlTags.ItemType? = null,
    val description: String? = null
)