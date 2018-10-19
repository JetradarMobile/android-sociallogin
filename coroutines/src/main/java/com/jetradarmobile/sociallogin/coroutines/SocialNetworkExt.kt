package com.jetradarmobile.sociallogin.coroutines

import android.app.Activity
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.SocialAccount
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun SocialNetwork.login(activity: Activity): SocialAccount = suspendCoroutine { continuation ->
  val callback = object : SocialLoginCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) = continuation.resume(account)
    override fun onLoginError(socialNetwork: SocialNetwork, error: SocialLoginError) = continuation.resumeWithException(error)
  }
  login(activity, callback)
}
