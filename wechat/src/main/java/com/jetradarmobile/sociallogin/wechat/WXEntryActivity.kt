package com.jetradarmobile.sociallogin.wechat


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

open class WXEntryActivity : Activity(), IWXAPIEventHandler {
  private lateinit var api: IWXAPI

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val appId = intent.extras?.getString(EXTRA_APP_ID) ?: ""
    api = WXAPIFactory.createWXAPI(this, appId, false).apply {
      registerApp(appId)
      handleIntent(intent, this@WXEntryActivity)
    }

    if (intent.getBooleanExtra(EXTRA_REGISTER, false)) {
      val req = SendAuth.Req()
      req.scope = "snsapi_userinfo"
      req.state = "none"
      api.sendReq(req)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    api.handleIntent(intent, this)
  }

  override fun onReq(req: BaseReq) {

  }

  override fun onResp(resp: BaseResp) {
    if (resp.errCode != BaseResp.ErrCode.ERR_OK) {
      val data = Intent()
      data.putExtra(EXTRA_ERROR, resp.errCode)
      data.putExtra(EXTRA_ERROR_STRING, resp.errStr)
      setResult(Activity.RESULT_CANCELED, data)
    } else if (resp is SendAuth.Resp) {
      val token = resp.code
      val data = Intent()
      data.putExtra(EXTRA_APP_ID, token)
      setResult(Activity.RESULT_OK, data)
    }
    finish()
  }

  companion object {
    const val EXTRA_REGISTER = "extra_register"
    const val EXTRA_APP_ID = "extra_app_id"
    const val EXTRA_ERROR = "extra_error"
    const val EXTRA_ERROR_STRING = "extra_error_string"

    const val REGISTER_CODE = 0x00fd
  }
}
