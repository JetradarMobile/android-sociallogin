[ ![Download](https://api.bintray.com/packages/jetradar/maven/android-sociallogin-vk/images/download.svg) ](https://bintray.com/jetradar/maven/android-sociallogin-vk/_latestVersion)

# SocialLogin Mail.ru module

```Groovy
dependencies {
    compile 'com.github.jetradarmobile:android-sociallogin-vk:x.y.z'
}
```

## Setup

Add your app id in resources. You should use exact same resource name as in sample below

```xml
<integer name="com_vk_sdk_AppId">YOUR_APP_ID</integer>
```

Then initialize VKSDK inside onCreate() method of your Application class

```Kotlin
class App: Application() {
 override fun onCreate() {
     super.onCreate()
     VKSdk.initialize(applicationContext)
 }
}
```
