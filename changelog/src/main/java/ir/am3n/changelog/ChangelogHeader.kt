package ir.am3n.changelog

/** Holds information about one "header", i.e. a <release> */
class ChangelogHeader(
    val version: String,
    val date: String? = null,
    val summary: String? = null
) : ChangelogItem()