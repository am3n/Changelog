package ir.am3n.changelog

/** Constants for xml tags and attributes (see res/xml/changelog.xml for an example) */

object XmlTags {

    const val RELEASE = "release"
    const val VERSION_NAME = "version"
    const val VERSION_CODE = "versioncode"
    const val SUMMARY = "summary"
    const val DATE = "date"

    enum class ItemType {

        NEW,
        CHANGE,
        INFO,
        FIX;

        companion object {
            fun type(name: String): ItemType {
                return valueOf(name.uppercase())
            }
            fun tags(): Array<String> {
                return values().map { it.name.lowercase() }.toTypedArray()
            }
        }

    }

}