package com.jetradarmobile.sociallogin.odnoklassniki

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialAuthError.UNKNOWN
import com.jetradarmobile.sociallogin.SocialNetwork
import org.json.JSONObject
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.OkListener
import ru.ok.android.sdk.util.OkAuthType

class OkNetwork(
    private val appId: String,
    private val appKey: String,
    private val redirectUrl: String,
    private val scope: List<String>
) : SocialNetwork, OkListener {
  override val code: String = CODE

  private var loginCallback: SocialAuthCallback? = null
  private var okInstance: Odnoklassniki? = null

  override fun login(activity: Activity, callback: SocialAuthCallback) {
    loginCallback = callback
    okInstance(activity).requestAuthorization(
        activity,
        redirectUrl,
        OkAuthType.ANY,
        *scope.toTypedArray())
  }

  override fun logout(activity: Activity, callback: SocialAuthCallback) {
    okInstance(activity).clearTokens()
    callback.onLogoutSuccess(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    okInstance?.onAuthActivityResult(requestCode, resultCode, data, this)
  }

  override fun onSuccess(json: JSONObject?) {
    loginCallback?.onLoginSuccess(this, createSocialToken(json))
  }

  override fun onError(okError: String?) {
    loginCallback?.onAuthError(this, if (!okError.isNullOrEmpty()) SocialAuthError(okError) else UNKNOWN)
  }

  private fun okInstance(activity: Activity): Odnoklassniki =
      okInstance ?: Odnoklassniki.createInstance(activity, appId, appKey).apply { okInstance = this }


  private fun createSocialToken(json: JSONObject?) = SocialAccount(
      token = json?.getString("access_token") ?: "",
      networkCode = CODE
  )

  companion object {
    const val CODE = "ok"
  }
}