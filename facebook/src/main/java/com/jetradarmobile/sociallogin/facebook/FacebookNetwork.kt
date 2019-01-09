package com.jetradarmobile.sociallogin.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork


class FacebookNetwork(private val permissions: List<String>) : SocialNetwork, FacebookCallback<LoginResult> {
  private val callbackManager = CallbackManager.Factory.create()

  override val code: String = CODE

  private var loginCallback: SocialLoginCallback? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    this.loginCallback = callback

    LoginManager.getInstance().registerCallback(callbackManager, this)

    val token = AccessToken.getCurrentAccessToken()
    val profile = Profile.getCurrentProfile()

    if (token == null) {
      LoginManager.getInstance().logInWithReadPermissions(activity, permissions)
    } else {
      val socialToken = createSocialToken(token, profile)
      loginCallback?.onLoginSuccess(this, socialToken)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  override fun logout(activity: Activity) {
    LoginManager.getInstance().logOut()
  }

  override fun onCancel() {
    loginCallback?.onLoginError(this, SocialLoginError.CANCELLED)
  }

  override fun onSuccess(result: LoginResult?) {
    val profile = Profile.getCurrentProfile()
    val token = result?.accessToken

    if (token != null) {
      val socialToken = createSocialToken(token, profile)
      loginCallback?.onLoginSuccess(this, socialToken)
    } else {
      loginCallback?.onLoginError(this, FacebookLoginError(FacebookLoginError.NoLogin))
    }
  }

  override fun onError(error: FacebookException?) {
    val message = error?.message ?: ""
    loginCallback?.onLoginError(this,
        if (message.isNotEmpty()) SocialLoginError(message) else SocialLoginError.CANCELLED)
  }

  private fun createSocialToken(accessToken: AccessToken, profile: Profile?) = SocialAccount(
      token = accessToken.token,
      networkCode = CODE,
      userId = accessToken.userId,
      userName = profile?.name ?: ""
  )

  companion object {
    const val CODE = "facebook"
  }

}