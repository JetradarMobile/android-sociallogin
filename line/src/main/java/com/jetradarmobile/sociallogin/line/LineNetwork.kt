package com.jetradarmobile.sociallogin.line

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.LineCredential
import com.linecorp.linesdk.LineProfile
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.api.LineApiClientBuilder
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi


class LineNetwork(private val channelId: String) : SocialNetwork {
  override val code: String = CODE

  private var loginCallback: SocialLoginCallback? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback

    val context = activity.applicationContext

    val params = LineAuthenticationParams.Builder()
        .scopes(listOf(Scope.PROFILE, Scope.OC_EMAIL))
        .build()
    var intent = LineLoginApi.getLoginIntent(context, channelId, params)
    if (intent.resolveActivity(activity.packageManager) != null) {
      activity.startActivityForResult(intent, REQUEST_CODE)
    } else {
      intent = LineLoginApi.getLoginIntentWithoutLineAppAuth(context, channelId, params)
      activity.startActivityForResult(intent, REQUEST_CODE)
    }
  }

  override fun logout(activity: Activity) {
    LineApiClientBuilder(activity, channelId).build().logout()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != REQUEST_CODE) return
    val result = LineLoginApi.getLoginResultFromIntent(data)
    when (result.responseCode) {
      LineApiResponseCode.SUCCESS -> loginCallback?.onLoginSuccess(this, makeToken(result.lineCredential, result.lineProfile))
      LineApiResponseCode.CANCEL  -> loginCallback?.onLoginError(this, SocialLoginError.CANCELLED)
      else                        -> loginCallback?.onLoginError(this, SocialLoginError(result.errorData.toString()))
    }
  }

  private fun makeToken(cred: LineCredential?, profile: LineProfile?) = SocialAccount(
      token = cred?.accessToken?.tokenString ?: "",
      networkCode = CODE,
      userId = profile?.userId ?: "",
      userName = profile?.displayName ?: ""
  )

  companion object {
    private const val REQUEST_CODE = 0x001c
    const val CODE = "line"
  }
}