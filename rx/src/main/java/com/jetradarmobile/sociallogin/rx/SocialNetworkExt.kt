package com.jetradarmobile.sociallogin.rx

import androidx.fragment.app.Fragment
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork
import io.reactivex.Completable
import io.reactivex.Single

fun SocialNetwork.login(fragment: Fragment): Single<SocialAccount> = Single.create { emitter ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) = emitter.onSuccess(account)
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) {}
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = emitter.onError(error)
  }
  login(fragment, callback)
}

fun SocialNetwork.logout(fragment: Fragment): Completable = Completable.create { emitter ->
  val callback = object : SocialAuthCallback {
    override fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount) {}
    override fun onLogoutSuccess(socialNetwork: SocialNetwork) = emitter.onComplete()
    override fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError) = emitter.onError(error)
  }
  login(fragment, callback)
}
