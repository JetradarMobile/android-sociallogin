package com.jetradarmobile.sociallogin.mailru

import android.os.Build
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object MailRuApi {
  private const val TOKEN_URL = "https://o2.mail.ru/"

  fun requestToken(
      clientId: String,
      clientSecret: String,
      grantType: String,
      code: String,
      redirectUri: String,
      debug: Boolean = false): Call = OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor(clientId, clientSecret))
      .apply { if (debug) addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) }
      .enableTls12OnPreLollipop()
      .build()
      .newCall(createTokenUrl(grantType, code, redirectUri))

  private fun createTokenUrl(grantType: String, code: String, redirectUri: String): Request = Request.Builder()
      .url(TOKEN_URL)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .post(FormBody.Builder()
          .add("grant_type", grantType)
          .add("code", code)
          .add("redirect_uri", redirectUri)
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