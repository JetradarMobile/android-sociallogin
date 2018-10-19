package com.jetradarmobile.sociallogin.line

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.SocialAccount
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.LineCredential
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.api.LineApiClientBuilder
import com.linecorp.linesdk.auth.LineLoginApi


class LineNetwork(private val channelId: String) : SocialNetwork {
  override val code: String = CODE

  private var loginCallback: SocialLoginCallback? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback

    val context = activity.applicationContext

    var intent = LineLoginApi.getLoginIntent(context, channelId)
    if (intent.resolveActivity(activity.packageManager) != null) {
      activity.startActivityForResult(intent, REQUEST_CODE)
    } else {
      intent = LineLoginApi.getLoginIntentWithoutLineAppAuth(context, channelId)
      activity.startActivityForResult(intent, REQUEST_CODE)
    }
  }

  override fun logout(activity: Activity) {
    val lineApi = LineApiClientBuilder(activity, channelId).build()
    lineApi.logout()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != REQUEST_CODE) return
    val result = LineLoginApi.getLoginResultFromIntent(data)
    when (result.responseCode) {
      LineApiResponseCode.SUCCESS -> {
        loginCallback?.onLoginSuccess(this, makeToken(result.lineCredential, result.lineProfile))
      }
      LineApiResponseCode.CANCEL  -> loginCallback?.onLoginError(this, SocialLoginError.CANCELLED)
      else                        -> {
        loginCallback?.onLoginError(this, SocialLoginError(result.errorData.toString()))
      }
    }
  }

  private fun makeToken(cred: LineCredential?, profile: LineProfile?) = SocialAccount(
      token = cred?.accessToken?.accessToken ?: "",
      userId = profile?.userId ?: "",
      userName = profile?.displayName ?: ""
  )

  companion object {
    private const val REQUEST_CODE = 0x001e
    const val CODE = "line"
  }
}