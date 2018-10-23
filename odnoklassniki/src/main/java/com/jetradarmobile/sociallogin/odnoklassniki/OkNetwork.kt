package com.jetradarmobile.sociallogin.odnoklassniki

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialLoginError.UNKNOWN
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
  override val requestCode: Int = REQUEST_CODE

  private var loginCallback: SocialLoginCallback? = null
  private var okInstance: Odnoklassniki? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback
    okInstance(activity).requestAuthorization(
        activity,
        redirectUrl,
        OkAuthType.ANY,
        *scope.toTypedArray())
  }

  override fun logout(activity: Activity) {
    okInstance(activity).clearTokens()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    okInstance?.onAuthActivityResult(requestCode, resultCode, data, this)
  }

  override fun onSuccess(json: JSONObject?) {
    loginCallback?.onLoginSuccess(this, createSocialToken(json))
  }

  override fun onError(okError: String?) {
    loginCallback?.onLoginError(this, if (!okError.isNullOrEmpty()) SocialLoginError(okError) else UNKNOWN)
  }

  private fun okInstance(activity: Activity): Odnoklassniki {
    if (okInstance == null) {
      okInstance = Odnoklassniki.createInstance(activity, appId, appKey)
    }
    return okInstance!!
  }

  private fun createSocialToken(json: JSONObject?) =
      SocialAccount(token = json?.getString("access_token") ?: "")

  companion object {
    const val CODE = "ok"
    const val REQUEST_CODE = 0x002a
  }
}