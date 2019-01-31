package com.jetradarmobile.sociallogin.vkontakte

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope

class VkNetwork(private val scope: List<VKScope>) : SocialNetwork, VKAuthCallback {
  override val code: String = CODE

  private var loginCallback: SocialAuthCallback? = null

  override fun login(activity: Activity, callback: SocialAuthCallback) {
    loginCallback = callback
    VK.login(activity, scope)
  }

  override fun logout(activity: Activity, callback: SocialAuthCallback) {
    VK.logout()
    callback.onLogoutSuccess(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    VK.onActivityResult(requestCode, resultCode, checkNotNull(data), this)
  }

  override fun onLogin(token: VKAccessToken) {
    loginCallback?.onLoginSuccess(this, createSocialToken(token))
  }

  override fun onLoginFailed(errorCode: Int) {
    val error = when (errorCode) {
      VKAuthCallback.UNKNOWN_ERROR -> SocialAuthError.UNKNOWN
      VKAuthCallback.AUTH_CANCELED -> SocialAuthError.CANCELLED
      else                         -> SocialAuthError("VK error: $errorCode")
    }
    loginCallback?.onAuthError(this, error)
  }

  private fun createSocialToken(vkAccessToken: VKAccessToken) = SocialAccount(
      token = vkAccessToken.accessToken,
      networkCode = CODE,
      userId = vkAccessToken.userId.toString()
  )

  companion object {
    const val CODE = "vk"
  }
}