package com.jetradarmobile.sociallogin

/**
 * Data class that store user data from social network
 *
 * @property token authorization token provided by social network api
 * @property secret oauth secret token, used in twitter
 * @property userId user identifier in social network
 * @property userName user display name
 * @property email user email
 * @property openid optional openID, used in wechat
 */
data class SocialAccount(
    val token: String,
    val networkCode: String,
    val secret: String = "",
    val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val openid: String = ""
)
