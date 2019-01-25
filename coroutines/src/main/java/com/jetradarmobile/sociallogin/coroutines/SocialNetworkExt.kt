package com.jetradarmobile.sociallogin.coroutines

import android.app.Activity
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun SocialNetwork.login(activity: Activity): SocialAccount = suspendCoroutine { continuation ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) = continuation.resume(account)
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) {}
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = continuation.resumeWithException(error)
  }
  login(activity, callback)
}

suspend fun SocialNetwork.logout(activity: Activity): Unit = suspendCoroutine { continuation ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) {}
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) = continuation.resume(Unit)
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = continuation.resumeWithException(error)
  }
  login(activity, callback)
}
