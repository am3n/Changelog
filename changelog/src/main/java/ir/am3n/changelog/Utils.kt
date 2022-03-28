package ir.am3n.changelog

import android.app.Activity
import android.os.Build

object Utils {

    /**
     * Extension function to retrieve the current version of the application from the package.
     * @return a pair <versionName, versionCode> (as set in the build.gradle file). Example: <"1.1", 3>
     */
    fun Activity.getAppVersion(): Pair<String, Long> {
        packageManager.getPackageInfo(packageName, 0).let {
            return Pair(
                it.versionName,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode
                else it.versionCode.toLong()
            )
        }
    }


}