package com.jetradarmobile.sociallogin.mailru

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.LayoutAlgorithm
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.jetradarmobile.sociallogin.SocialLoginError
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.ac_mail_ru_login.progressBar
import kotlinx.android.synthetic.main.ac_mail_ru_login.toolbar
import kotlinx.android.synthetic.main.ac_mail_ru_login.toolbarShadow
import kotlinx.android.synthetic.main.ac_mail_ru_login.webView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.UUID

class MailRuLoginActivity : AppCompatActivity() {

  private var state: String? = null
  private var tokenCall: Call? = null
  private lateinit var clientId: String
  private lateinit var clientSecret: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.ac_mail_ru_login)
    loadArguments()
    prepareWebView()
    prepareToolbar()
    loadSite()
  }

  override fun onDestroy() {
    super.onDestroy()
    destroyWebView()
    tokenCall?.cancel()
  }

  private fun loadArguments() {
    intent.apply {
      clientId = getStringExtra(CLIENT_ID) ?: ""
      clientSecret = getStringExtra(CLIENT_SECRET) ?: ""
    }

    if (clientId.isBlank() || clientSecret.isBlank()) authFailed(Throwable("Client credentials is empty"))
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun prepareWebView() {
    webView.apply {
      setInitialScale(1)
      scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
      webViewClient = MailWebViewClient(this@MailRuLoginActivity)
      webChromeClient = MailWebChromeClient(this@MailRuLoginActivity)
      settings.apply {
        javaScriptEnabled = true
        useWideViewPort = true
        loadWithOverviewMode = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
          layoutAlgorithm = LayoutAlgorithm.SINGLE_COLUMN
        }
        javaScriptCanOpenWindowsAutomatically = true
        builtInZoomControls = true
        displayZoomControls = false
        domStorageEnabled = true
        setSupportZoom(true)
        setSupportMultipleWindows(true)
      }
    }
    CookieManager.getInstance().setAcceptCookie(true)
  }

  private fun prepareToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)
    toolbar.setNavigationOnClickListener { finish() }
    toolbarShadow.visibility = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) View.VISIBLE else View.GONE
  }

  private fun loadSite() {
    state = UUID.randomUUID().toString()
    val url = "$LOGIN_URL&client_id=$clientId&state=$state"
    webView.loadUrl(url)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intent?.apply { processResult(intent.data) }
  }

  private fun processResult(uri: Uri?) {
    uri?.apply {
      if ("$scheme://$host$path" != REDIRECT_URL) return

      if (uri.getQueryParameter(STATE) != state) {
        authFailed(Throwable("State check failed."))
        return
      }

      val code = uri.getQueryParameter(CODE)
      if (code != null && !code.isBlank()) {
        requestAccessToken(code)
      } else {
        authFailed(Throwable("Wrong authorization code."))
      }
    }
  }

  private fun requestAccessToken(code: String) {
    tokenCall = MailRuApi.requestToken(clientId, clientSecret, GRANT_TYPE_CODE, code, REDIRECT_URL).apply {
      enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) = authFailed(e)

        override fun onResponse(call: Call, response: Response) {
          val body = response.body()
          if (response.isSuccessful && body != null) {
            Moshi.Builder().build().adapter(TokenBean::class.java).fromJson(body.source())?.let { authSuccess(it) }
                ?: authFailed(SocialLoginError.UNKNOWN)
          } else {
            authFailed(SocialLoginError.UNKNOWN)
          }
        }
      })
    }
  }

  private fun authSuccess(token: TokenBean) {
    val data = Intent().putExtra(TOKEN_BEAN, token)
    setResult(Activity.RESULT_OK, data)
    finish()
  }

  private fun authFailed(error: Throwable) {
    val result = Intent()
    result.putExtra(ERROR_MESSAGE, error.message)
    setResult(Activity.RESULT_CANCELED, result)
    finish()
  }

  private fun destroyWebView() {
    webView.apply {
      clearHistory()
      clearCache(true)
      destroy()
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      CookieManager.getInstance().removeAllCookie()
    } else {
      CookieManager.getInstance().removeAllCookies {}
    }
  }

  companion object {
    private const val STATE = "state"
    private const val CODE = "code"

    private const val REDIRECT_URL = "https://auth.avs.io/auth/v2/mail_ru/callback"
    private const val LOGIN_URL = "https://o2.mail.ru/login" +
        "?response_type=code" +
        "&scope=userinfo" +
        "&redirect_uri=$REDIRECT_URL"

    const val CLIENT_ID = "client_id"
    const val CLIENT_SECRET = "client_secret"
    const val ERROR_MESSAGE = "error"

    const val GRANT_TYPE_CODE = "authorization_code"
    const val TOKEN_BEAN = "token_bean"
  }

  private class MailWebViewClient(private val activity: MailRuLoginActivity) : WebViewClient() {

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
      activity.processResult(request?.url)
      return false
    }

    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
      activity.processResult(Uri.parse(url))
      return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
      super.onPageFinished(view, url)
      activity.processResult(Uri.parse(url))
    }
  }

  private class MailWebChromeClient(private val activity: MailRuLoginActivity) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
      super.onProgressChanged(view, newProgress)
      activity.apply {
        if (progressBar != null) {
          progressBar.progress = newProgress
          progressBar.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
        }
      }
    }

    override fun onCloseWindow(window: WebView?) {
      super.onCloseWindow(window)
      activity.finish()
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
      if (consoleMessage?.message() == "Scripts may close only the windows that were opened by it.") {
        activity.finish()
      }
      return super.onConsoleMessage(consoleMessage)
    }
  }
}