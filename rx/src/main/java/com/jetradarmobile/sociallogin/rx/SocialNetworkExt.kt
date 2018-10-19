package com.jetradarmobile.sociallogin.rx

import android.app.Activity
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.SocialAccount
import io.reactivex.Single

fun SocialNetwork.login(activity: Activity): Single<SocialAccount> = Single.create { subscriber ->
  val callback = object : SocialLoginCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) = subscriber.onSuccess(account)
    override fun onLoginError(socialNetwork: SocialNetwork, error: SocialLoginError) = subscriber.onError(error)
  }
  login(activity, callback)
}