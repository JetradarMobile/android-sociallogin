[ ![Download](https://api.bintray.com/packages/jetradar/maven/android-sociallogin-facebook/images/download.svg) ](https://bintray.com/jetradar/maven/android-sociallogin-facebook/_latestVersion)

# SocialLogin Facebook module

```Groovy
dependencies {
    compile 'com.github.jetradarmobile:android-sociallogin-facebook:x.y.z'
}
```

## Setup

To use, add to res/values/strings.xml your Facebook App keys

```xml
<string name="facebook_app_id">YOUR_APP_ID</string>
```

Then add this strings to your AndroidManifest.xml inside <application> section

```xml
<application>

    <meta-data
        android:name="com.facebook.sdk.ApplicationId"
        android:value="@string/facebook_app_id"/>

    <!-- Other tags ... -->

</application>
```