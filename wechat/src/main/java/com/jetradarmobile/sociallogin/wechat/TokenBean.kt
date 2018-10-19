package com.jetradarmobile.sociallogin.wechat

import com.squareup.moshi.Json

internal data class TokenBean(
    @Json(name = "access_token") val token: String,
    @Json(name = "openid") val openId: String
)