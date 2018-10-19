package com.jetradarmobile.sociallogin

open class SocialLoginError(reason: Reason, cause: Throwable? = null) : Throwable(reason.message, cause) {
  constructor(message: String) : this(Reason(message))

  object CANCELLED : SocialLoginError(Reason.CANCEL)
  object EMPTY : SocialLoginError(Reason.EMPTY)
  object UNKNOWN : SocialLoginError(Reason.UNKNOWN)

  open class Reason(val message: String) {
    object CANCEL : Reason("Authorization process was cancelled")
    object EMPTY : Reason("Auth token is null or empty")
    object UNKNOWN : Reason("Unknown auth error")
  }
}