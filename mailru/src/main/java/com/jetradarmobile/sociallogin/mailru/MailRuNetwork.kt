package com.jetradarmobile.sociallogin.mailru

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.webkit.CookieManager
import androidx.fragment.app.Fragment
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork

class MailRuNetwork(
    private val clientId: String,
    private val clientSecret: String
) : SocialNetwork {

  override val code: String = CODE

  private var loginCallback: SocialAuthCallback? = null

  override fun login(fragment: Fragment, callback: SocialAuthCallback) {
    loginCallback = callback

    val context = fragment.requireContext()
    val intent = Intent(context, MailRuLoginActivity::class.java).apply {
      putExtra(MailRuLoginActivity.CLIENT_ID, clientId)
      putExtra(MailRuLoginActivity.CLIENT_SECRET, clientSecret)
    }
    fragment.startActivityForResult(intent, REQUEST_CODE)
  }

  override fun logout(fragment: Fragment, callback: SocialAuthCallback) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      CookieManager.getInstance().removeAllCookie()
    } else {
      CookieManager.getInstance().removeAllCookies {}
    }
    callback.onLogoutSuccess(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != REQUEST_CODE) {
      loginCallback?.onAuthError(this, SocialAuthError.CANCELLED)
    }

    when (resultCode) {
      Activity.RESULT_OK -> {
        val token = data?.getParcelableExtra<TokenBean>(MailRuLoginActivity.TOKEN_BEAN)
        if (token == null || token.accessToken.isBlank()) {
          loginCallback?.onAuthError(this, SocialAuthError.EMPTY)
        } else {
          loginCallback?.onLoginSuccess(this, makeToken(token.accessToken))
        }
      }
      else               -> {
        val errorText = data?.getStringExtra(MailRuLoginActivity.ERROR_MESSAGE) ?: ""
        val error = if (errorText.isNotEmpty()) SocialAuthError(errorText) else SocialAuthError.CANCELLED
        loginCallback?.onAuthError(this, error)
      }
    }
  }

  private fun makeToken(token: String?) = SocialAccount(token ?: "", CODE)

  companion object {
    private const val REQUEST_CODE = 0x001e
    const val CODE = "mail_ru"
  }
}