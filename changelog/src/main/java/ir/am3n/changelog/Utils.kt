package ir.am3n.changelog

import android.app.Activity
import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Build
import org.xmlpull.v1.XmlPullParser

object Utils {

    /**
     * Extension function to retrieve the current version of the application from the package.
     * @return a pair <versionName, versionCode> (as set in the build.gradle file). Example: <"1.1", 3>
     */
    fun Activity.getAppVersion(): Pair<String, Long> {
        packageManager.getPackageInfo(packageName, 0).let {
            return Pair(
                it.versionName,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    it.longVersionCode
                } else {
                    it.versionCode.toLong()
                }
            )
        }
    }


    /**
     * Read the changelog.xml and create a list of [ChangelogItem] and [ChangelogHeader].
     * @param context: application context
     * @param resourceId: the name of the changelog file, default to R.xml.changelog
     * @param lastVersionCode: the latest version displayed
     * @param version: the lowest release to display
     * @return the list of [ChangelogItem], in the order of the [resourceId] file (most to less recent)
     */
    fun loadChangelog(
        context: Context?,
        resourceId: Int?,
        lastVersionCode: Long,
        version: Int
    ): MutableList<ChangelogItem> {
        val changelogItems = mutableListOf<ChangelogItem>()
        try {
            context?.resources?.getXml(resourceId!!)?.let { xml ->
                while (xml.eventType != XmlPullParser.END_DOCUMENT) {
                    if (xml.eventType == XmlPullParser.START_TAG && xml.name == XmlTags.RELEASE) {
                        val releaseVersion = Integer.parseInt(xml.getAttributeValue(null, XmlTags.VERSION_CODE))
                        if (releaseVersion <= lastVersionCode && version == -1)
                            break
                        changelogItems.addAll(parseReleaseTag(context, xml))
                        if (releaseVersion <= version && version >= 0)
                            break
                    } else {
                        xml.next()
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return changelogItems
    }

    /**
     * Parse one release tag attribute.
     * @param context application context
     * @param xml the xml resource parser. Its cursor should be at a release tag.
     * @return a list containing one [ChangelogHeader] and zero or more [ChangelogItem]
     */
    private fun parseReleaseTag(context: Context?, xml: XmlResourceParser): MutableList<ChangelogItem> {
        require(xml.name == XmlTags.RELEASE && xml.eventType == XmlPullParser.START_TAG)
        val items = mutableListOf<ChangelogItem>()
        // parse header
        items.add(
            ChangelogHeader(
                version = xml.getAttributeValue(null, XmlTags.VERSION_NAME) ?: "X.X",
                date = xml.getAttributeValue(null, XmlTags.DATE),
                summary = xml.getAttributeValue(null, XmlTags.SUMMARY)
            )
        )
        xml.next()
        // parse changes
        var tag: String? = null
        var icon: String? = null
        while (xml.name in XmlTags.ItemType.tags() || xml.eventType == XmlPullParser.TEXT) {
            if (xml.eventType == XmlPullParser.START_TAG) {
                tag = xml.name
                if (tag.uppercase() == XmlTags.ItemType.CUSTOM.name) {
                    icon = xml.getAttributeValue(null, "icon")
                }
            } else if (xml.eventType == XmlPullParser.TEXT) {
                items.add(ChangelogItem(
                    type = XmlTags.ItemType.type(tag!!),
                    description = xml.text,
                    icon = try {
                        if (!icon.isNullOrBlank())
                            context?.resources?.getIdentifier(icon.replace("R.drawable.", ""), "drawable", context.packageName)
                        else null
                    } catch (t: Throwable) {
                        null
                    }
                ))
            }
            xml.next()
        }
        return items
    }

}