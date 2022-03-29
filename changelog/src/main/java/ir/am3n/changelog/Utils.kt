package ir.am3n.changelog

import android.app.Activity
import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Build
import android.text.format.DateFormat
import org.xmlpull.v1.XmlPullParser
import java.sql.Date
import java.text.ParseException

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
        version: Int = Changelog.ALL_VERSIONS
    ): MutableList<ChangelogItem> {
        val changelogItems = mutableListOf<ChangelogItem>()
        try {
            context?.resources?.getXml(resourceId!!)?.let { xml ->
                while (xml.eventType != XmlPullParser.END_DOCUMENT) {
                    if (xml.eventType == XmlPullParser.START_TAG && xml.name == XmlTags.RELEASE) {
                        val releaseVersion = Integer.parseInt(xml.getAttributeValue(null,
                            XmlTags.VERSION_CODE
                        ))
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
     * @param context the calling activity
     * @param xml the xml resource parser. Its cursor should be at a release tag.
     * @return a list containing one [ChangelogHeader] and zero or more [ChangelogItem]
     */
    private fun parseReleaseTag(context: Context, xml: XmlResourceParser): MutableList<ChangelogItem> {
        require(xml.name == XmlTags.RELEASE && xml.eventType == XmlPullParser.START_TAG)
        val items = mutableListOf<ChangelogItem>()
        // parse header
        items.add(
            ChangelogHeader(
                version = xml.getAttributeValue(null, XmlTags.VERSION_NAME) ?: "X.X",
                date = xml.getAttributeValue(null, XmlTags.DATE)?.let { parseDate(context, it) },
                summary = xml.getAttributeValue(null, XmlTags.SUMMARY)
            )
        )
        xml.next()
        // parse changes
        while (xml.name == XmlTags.ITEM || xml.eventType == XmlPullParser.TEXT) {
            if (xml.eventType == XmlPullParser.TEXT) {
                items.add(ChangelogItem(xml.text))
            }
            xml.next()
        }
        return items
    }

    /**
     * Format a date string.
     * @param context The calling activity
     * @param dateString The date string, in ISO format (YYYY-MM-dd)
     * @return The date formatted using the system locale, or [dateString] if the parsing failed.
     */
    private fun parseDate(context: Context, dateString: String): String {
        return try {
            val parsedDate = Date.valueOf(dateString)
            DateFormat.getDateFormat(context).format(parsedDate)
        } catch (_: ParseException) {
            // wrong date format... Just keep the string as is
            dateString
        }
    }


}