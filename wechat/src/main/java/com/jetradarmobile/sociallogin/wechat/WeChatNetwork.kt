package com.jetradarmobile.sociallogin.wechat

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialAuthError
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.wechat.WXLoginError.UNKNOWN
import com.squareup.moshi.Moshi
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class WeChatNetwork(
    private val appId: String,
    private val appSecret: String
) : SocialNetwork {
  override val code: String = CODE

  private var loginCallback: SocialAuthCallback? = null
  private var tokenCall: Call? = null

  override fun login(fragment: Fragment, callback: SocialAuthCallback) {
    loginCallback = callback

    val context = fragment.requireContext()
    val api = WXAPIFactory.createWXAPI(context, appId)
    api.sendReq(SendAuth.Req().apply { })
    val intent = Intent(context, WXEntryActivity::class.java)
    intent.putExtra(WXEntryActivity.EXTRA_REGISTER, true)
    intent.putExtra(WXEntryActivity.EXTRA_APP_ID, appId)
    fragment.startActivityForResult(intent, WXEntryActivity.REGISTER_CODE)
  }

  override fun logout(fragment: Fragment, callback: SocialAuthCallback) {
    val api = WXAPIFactory.createWXAPI(fragment.requireContext(), appId)
    api.unregisterApp()
    callback.onLogoutSuccess(this)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != WXEntryActivity.REGISTER_CODE) return


    if (resultCode == Activity.RESULT_OK) {
      requestAccessToken(data?.getStringExtra(WXEntryActivity.EXTRA_APP_ID) ?: "")
    } else {
      val error = data?.getIntExtra(WXEntryActivity.EXTRA_ERROR, Int.MIN_VALUE) ?: Int.MIN_VALUE
      val reason = when (error) {
        BaseResp.ErrCode.ERR_USER_CANCEL -> SocialAuthError.Reason.CANCEL
        BaseResp.ErrCode.ERR_AUTH_DENIED -> WXLoginError.DENY
        BaseResp.ErrCode.ERR_UNSUPPORT   -> WXLoginError.UNSUPPORTED
        else                             -> WXLoginError.UNKNOWN
      }
      loginCallback?.onAuthError(this, SocialAuthError(reason))
    }
  }


  private fun requestAccessToken(code: String) {
    if (code.isEmpty()) {
      loginCallback?.onAuthError(this, WXLoginError(UNKNOWN))
      return
    }
    tokenCall = WeChatApi.requestToken(appId, appSecret, code).apply {
      enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) = handleError(e)

        override fun onResponse(call: Call, response: Response) {
          val body = response.body()
          if (response.isSuccessful && body != null) {
            Moshi.Builder().build().adapter(TokenBean::class.java).fromJson(body.source())?.let { handleSuccess(it) }
                ?: handleError(SocialAuthError.UNKNOWN)
          } else {
            handleError(SocialAuthError.UNKNOWN)
          }
        }
      })
    }
  }

  private fun handleSuccess(tokenBean: TokenBean) {
    loginCallback?.onLoginSuccess(this, SocialAccount(
        token = tokenBean.token,
        networkCode = CODE,
        openid = tokenBean.openId
    ))
  }

  private fun handleError(error: Throwable) {
    loginCallback?.onAuthError(this, WXLoginError(UNKNOWN, error))
  }

  companion object {
    const val CODE = "wechat"
  }
}