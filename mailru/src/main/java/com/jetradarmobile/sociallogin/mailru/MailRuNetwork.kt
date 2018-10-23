package com.jetradarmobile.sociallogin.mailru

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.webkit.CookieManager
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
import com.jetradarmobile.sociallogin.SocialNetwork

class MailRuNetwork(
    private val clientId: String,
    private val clientSecret: String
) : SocialNetwork {

  override val code: String = CODE
  override val requestCode: Int = REQUEST_CODE

  private var loginCallback: SocialLoginCallback? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {

    loginCallback = callback

    val intent = Intent(activity, MailRuLoginActivity::class.java)
    intent.putExtra(MailRuLoginActivity.CLIENT_ID, clientId)
    intent.putExtra(MailRuLoginActivity.CLIENT_SECRET, clientSecret)
    activity.startActivityForResult(intent, REQUEST_CODE)
  }

  override fun logout(activity: Activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      CookieManager.getInstance().removeAllCookie()
    } else {
      CookieManager.getInstance().removeAllCookies {}
    }
    onActivityResult(REQUEST_CODE, Activity.RESULT_OK, null)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != REQUEST_CODE) {
      loginCallback?.onLoginError(this, SocialLoginError.CANCELLED)
    }

    when (resultCode) {
      Activity.RESULT_OK -> {
        val token = data?.getParcelableExtra<TokenBean>(MailRuLoginActivity.TOKEN_BEAN)
        if (token == null || token.accessToken.isBlank()) {
          loginCallback?.onLoginError(this, SocialLoginError.EMPTY)
        } else {
          loginCallback?.onLoginSuccess(this, makeToken(token.accessToken))
        }
      }
      else               -> {
        val errorText = data?.getStringExtra(MailRuLoginActivity.ERROR_MESSAGE) ?: ""
        val error = if (errorText.isNotEmpty()) SocialLoginError(errorText) else SocialLoginError.CANCELLED
        loginCallback?.onLoginError(this, error)
      }
    }
  }

  private fun makeToken(token: String?) = SocialAccount(token ?: "")

  companion object {
    const val REQUEST_CODE = 0x001e
    const val CODE = "mail_ru"
  }
}