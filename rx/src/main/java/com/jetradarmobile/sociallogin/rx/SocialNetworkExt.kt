package com.jetradarmobile.sociallogin.rx

import android.app.Activity
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork
import io.reactivex.Completable
import io.reactivex.Single

fun SocialNetwork.login(activity: Activity): Single<SocialAccount> = Single.create { emitter ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) = emitter.onSuccess(account)
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) {}
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = emitter.onError(error)
  }
  login(activity, callback)
}

fun SocialNetwork.logout(activity: Activity): Completable = Completable.create { emitter ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) {}
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) = emitter.onComplete()
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = emitter.onError(error)
  }
  logout(activity, callback)
}
