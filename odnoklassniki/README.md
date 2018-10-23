[ ![Download](https://api.bintray.com/packages/jetradar/maven/android-sociallogin-ok/images/download.svg) ](https://bintray.com/jetradar/maven/android-sociallogin-ok/_latestVersion)

# SocialLogin OK module

```Groovy
dependencies {
    compile 'com.github.jetradarmobile:android-sociallogin-ok:x.y.z'
}
```

## Setup

Include Odnoklasniki Activity in your AndroidManifest.xml

```xml
<activity
    android:name="ru.ok.android.sdk.OkAuthActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>

        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>

        <data
            android:host="ok{YOUR_APP_ID}"
            android:scheme="okauth"/>
    </intent-filter>
</activity>
```

Then replace {YOUR_APP_ID} with your app id from odnokassniki. Should looks like:

```xml
<data
    android:host="ok1234567890"
    android:scheme="okauth"/>
```