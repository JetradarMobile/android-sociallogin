package com.jetradarmobile.sociallogin.mailru

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

internal class AuthInterceptor(
    private val name: String,
    private val password: String
) : Interceptor {

  override fun intercept(chain: Chain): Response = chain.proceed(chain.request().newBuilder()
      .header("Authorization", Credentials.basic(name, password))
      .build()
  )
}