# Changelog

![MinAPI](https://img.shields.io/badge/API-21%2B-blue)
[![](https://jitpack.io/v/am3n/Changelog.svg)](https://jitpack.io/#am3n/Changelog)

Changelog is a library for Android API 21+. 
It helps developers display the history of changes in their applications.

You can find a sample code of Changelog in this repository.


Installation
-------
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
```groovy
dependencies {
    implementation "com.github.am3n:Changelog:NEWEST-VERSION"
}
```


Usage
-------
```kotlin
Changelog.present(
    activity = this,
    presentMode = PresentMode.IF_NEEDED,
    presentFrom = Changelog.NEW_VERSIONS,
    title = string(R.string.whatsnew),
    buttonText = "Ok",
    changelogId = R.xml.changelog,
    onDismissOrIgnoredListener = {}
)
```


Upcoming
-------
* Add layout directions option to support rtl languages
* Add custom typeface option
* Add background drawable or color option
* Add presentIn option to show as dialog or bottom sheet or ...
* Add custom animations option
* Add button text color option
* Add cell image custom resource option
* Add cell image custom tint option
* Add dark mode option
* Add some features from 'Credits' libraries


Contribution
-------
If you've found an error in the library or sample, please file an issue.
Patches are encouraged, and may be submitted by forking this project and submitting a pull request.


Credits
-------
* https://github.com/MFlisar/changelog
* https://github.com/furkanakdemir/noticeboard
* https://github.com/anderscheow/WhatsNew
* https://github.com/cketti/ckChangeLog


License
-------

    Copyright 2022 Amirhosein Barati

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.