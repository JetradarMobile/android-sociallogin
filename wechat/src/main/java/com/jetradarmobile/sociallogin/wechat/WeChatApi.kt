package com.jetradarmobile.sociallogin.wechat

import android.os.Build
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object WeChatApi {
  private const val TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token"

  fun requestToken(
      clientId: String,
      clientSecret: String,
      code: String,
      debug: Boolean = false): Call = OkHttpClient.Builder()
      .apply { if (debug) addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) }
      .enableTls12OnPreLollipop()
      .build()
      .newCall(createTokenUrl(clientId, clientSecret, code))

  private fun createTokenUrl(clientId: String, clientSecret: String, code: String): Request = Request.Builder()
      .url(TOKEN_URL)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .post(FormBody.Builder()
          .add("appid", clientId)
          .add("secret", clientSecret)
          .add("code", code)
          .add("grant_type", "authorization_code")
          .build()
      )
      .build()


  private fun OkHttpClient.Builder.enableTls12OnPreLollipop() = apply {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      try {
        val trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            .apply { init(null as KeyStore?) }
            .trustManagers
        val trustManager = trustManagers.getOrNull(0) as? X509TrustManager
            ?: throw IllegalStateException("Unexpected default trust managers: $trustManagers")
        sslSocketFactory(Tls12SocketFactory(), trustManager)
      } catch (ignored: Exception) {
      }
    }
  }
}