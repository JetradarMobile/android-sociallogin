package com.jetradarmobile.sociallogin.mailru

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class TokenBean(
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "expires_in") val tokenExpiresIn: Long
) : Parcelable