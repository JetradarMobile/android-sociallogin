package com.jetradarmobile.sociallogin.facebook

import android.content.Intent
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork


class FacebookNetwork(private val permissions: List<String>) : SocialNetwork, FacebookCallback<LoginResult> {
  private val callbackManager = CallbackManager.Factory.create()

  override val code: String = CODE

  private var loginCallback: SocialAuthCallback? = null

  override fun login(fragment: Fragment, callback: SocialAuthCallback) {
    this.loginCallback = callback

    LoginManager.getInstance().registerCallback(callbackManager, this)

    val token = AccessToken.getCurrentAccessToken()
    val profile = Profile.getCurrentProfile()

    if (token == null) {
      LoginManager.getInstance().logInWithReadPermissions(fragment, permissions)
    } else {
      val socialToken = createSocialToken(token, profile)
      loginCallback?.onLoginSuccess(this, socialToken)
    }
  }

  override fun logout(fragment: Fragment, callback: SocialAuthCallback) {
    LoginManager.getInstance().logOut()
    callback.onLogoutSuccess(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  override fun onCancel() {
    loginCallback?.onAuthError(this, SocialAuthError.CANCELLED)
  }

  override fun onSuccess(result: LoginResult?) {
    val profile = Profile.getCurrentProfile()
    val token = result?.accessToken

    if (token != null) {
      val socialToken = createSocialToken(token, profile)
      loginCallback?.onLoginSuccess(this, socialToken)
    } else {
      loginCallback?.onAuthError(this, FacebookLoginError(FacebookLoginError.NoLogin))
    }
  }

  override fun onError(error: FacebookException?) {
    val message = error?.message ?: ""
    loginCallback?.onAuthError(this,
        if (message.isNotEmpty()) SocialAuthError(message) else SocialAuthError.CANCELLED)
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