package com.jetradarmobile.sociallogin.vkontakte

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError


class VkNetwork(private val scope: List<String>) : SocialNetwork, VKCallback<VKAccessToken> {
  override val code: String = CODE

  private var loginCallback: SocialLoginCallback? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback
    VKSdk.login(activity, *scope.toTypedArray())
  }

  override fun logout(activity: Activity) {
    VKSdk.logout()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    VKSdk.onActivityResult(requestCode, resultCode, data, this)
  }

  override fun onResult(token: VKAccessToken?) {
    loginCallback?.let {
      if (token != null) {
        it.onLoginSuccess(this, createSocialToken(token))
      } else {
        it.onLoginError(this, VkLoginError(VkLoginError.NoLogin))
      }
    }
  }

  override fun onError(vkError: VKError?) {
    val error = when {
      vkError == null                          -> SocialLoginError.UNKNOWN
      vkError.errorCode == VKError.VK_CANCELED -> SocialLoginError.CANCELLED
      else                                     -> {
        val message = vkError.let { "[${it.errorCode}] - ${it.errorReason} : ${it.errorMessage}" }
        SocialLoginError(message)
      }
    }
    loginCallback?.onLoginError(this, error)
  }

  private fun createSocialToken(vkAccessToken: VKAccessToken?) = SocialAccount(
      token = vkAccessToken?.accessToken ?: "",
      networkCode = CODE,
      userId = vkAccessToken?.userId ?: "",
      email = vkAccessToken?.email ?: ""
  )

  companion object {
    const val CODE = "vk"
  }
}