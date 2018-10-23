[ ![Download](https://api.bintray.com/packages/jetradar/maven/android-sociallogin/images/download.svg) ](https://bintray.com/jetradar/maven/android-sociallogin/_latestVersion)

# Social Login
### Library for authorizing in popular social networks

To include 'core' module add following string to your module build.gradle file

```Groovy
dependencies {
    compile 'com.github.jetradarmobile:android-sociallogin:x.y.z'
}
```
where ```x.y.z``` is the version of lib. You can find latest version in the badge on top of the page

Library is modular, you can include only those socials that you need. Just add appropriate
dependency to build.gradle file. But remember, that each module depends of 'core' module

For example if you need a facebook, you should add this line in build.gradle file

```Groovy
dependencies {
    ...
    compile 'com.github.jetradarmobile:android-sociallogin-facebook:x.y.z'
}
```

There is a list of modules:

```Groovy
compile 'com.github.jetradarmobile:android-sociallogin-facebook:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-google:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-line:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-mailru:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-odnoklassniki:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-twitter:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-vk:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-wechat:x.y.z'

compile 'com.github.jetradarmobile:android-sociallogin-rxJava2:x.y.z'
compile 'com.github.jetradarmobile:android-sociallogin-coroutines:x.y.z'

```

If module you want is not present here, you can implement it by yourself
Also you can contribute, by creating pull-requests =)

For instructions see appropriate social network module.