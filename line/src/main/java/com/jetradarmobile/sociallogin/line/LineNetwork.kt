package com.jetradarmobile.sociallogin.line

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
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

  private var loginCallback: SocialAuthCallback? = null

  override fun login(activity: Activity, callback: SocialAuthCallback) {
    loginCallback = callback

    val params = LineAuthenticationParams.Builder()
        .scopes(listOf(Scope.PROFILE, Scope.OC_EMAIL))
        .build()
    var intent = LineLoginApi.getLoginIntent(activity, channelId, params)
    if (intent.resolveActivity(activity.packageManager) != null) {
      activity.startActivityForResult(intent, REQUEST_CODE)
    } else {
      intent = LineLoginApi.getLoginIntentWithoutLineAppAuth(activity, channelId, params)
      activity.startActivityForResult(intent, REQUEST_CODE)
    }
  }

  override fun logout(activity: Activity, callback: SocialAuthCallback) {
    val response = LineApiClientBuilder(activity, channelId).build().logout()
    if (response.isSuccess) callback.onLogoutSuccess(this)
    else callback.onAuthError(this, SocialAuthError.UNKNOWN)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != REQUEST_CODE) return
    val result = LineLoginApi.getLoginResultFromIntent(data)
    when (result.responseCode) {
      LineApiResponseCode.SUCCESS -> loginCallback?.onLoginSuccess(this, makeToken(result.lineCredential, result.lineProfile))
      LineApiResponseCode.CANCEL  -> loginCallback?.onAuthError(this, SocialAuthError.CANCELLED)
      else                        -> loginCallback?.onAuthError(this, SocialAuthError(result.errorData.toString()))
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