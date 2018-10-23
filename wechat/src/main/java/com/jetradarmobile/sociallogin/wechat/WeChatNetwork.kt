package com.jetradarmobile.sociallogin.wechat

import android.app.Activity
import android.content.Intent
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialLoginError
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
    private val clientId: String,
    private val clientSecret: String,
    private val token: String
) : SocialNetwork {
  override val code: String = CODE
  override val requestCode: Int = REQUEST_CODE

  private var loginCallback: SocialLoginCallback? = null
  private var tokenCall: Call? = null

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback

    val api = WXAPIFactory.createWXAPI(activity, token)
    api.sendReq(SendAuth.Req().apply { })
    val intent = Intent(activity, WXEntryActivity::class.java)
    intent.putExtra(WXEntryActivity.EXTRA_REGISTER, true)
    intent.putExtra(WXEntryActivity.EXTRA_TOKEN, token)
    activity.startActivityForResult(intent, WXEntryActivity.REGISTER_CODE)
  }

  override fun logout(activity: Activity) {
    val api = WXAPIFactory.createWXAPI(activity, token)
    api.unregisterApp()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != WXEntryActivity.REGISTER_CODE) return


    if (resultCode == Activity.RESULT_OK) {
      requestAccessToken(data?.getStringExtra(WXEntryActivity.EXTRA_TOKEN) ?: "")
    } else {
      val error = data?.getIntExtra(WXEntryActivity.EXTRA_ERROR, Int.MIN_VALUE) ?: Int.MIN_VALUE
      val reason = when (error) {
        BaseResp.ErrCode.ERR_USER_CANCEL -> SocialLoginError.Reason.CANCEL
        BaseResp.ErrCode.ERR_AUTH_DENIED -> WXLoginError.DENY
        BaseResp.ErrCode.ERR_UNSUPPORT   -> WXLoginError.UNSUPPORTED
        else                             -> WXLoginError.UNKNOWN
      }
      loginCallback?.onLoginError(this, SocialLoginError(reason))
    }
  }


  private fun requestAccessToken(code: String) {
    if (code.isEmpty()) {
      loginCallback?.onLoginError(this, WXLoginError(UNKNOWN))
      return
    }
    tokenCall = WeChatApi.requestToken(clientId, clientSecret, code).apply {
      enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) = handleError(e)

        override fun onResponse(call: Call, response: Response) {
          val body = response.body()
          if (response.isSuccessful && body != null) {
            Moshi.Builder().build().adapter(TokenBean::class.java).fromJson(body.source())?.let { handleSuccess(it) }
                ?: handleError(SocialLoginError.UNKNOWN)
          } else {
            handleError(SocialLoginError.UNKNOWN)
          }
        }
      })
    }
  }

  private fun handleSuccess(tokenBean: TokenBean) {
    loginCallback?.onLoginSuccess(this, SocialAccount(token = tokenBean.token, openid = tokenBean.openId))
  }

  private fun handleError(error: Throwable) {
    loginCallback?.onLoginError(this, WXLoginError(UNKNOWN, error))
  }

  companion object {
    const val CODE = "wechat"
    const val REQUEST_CODE = 0x002d
  }
}