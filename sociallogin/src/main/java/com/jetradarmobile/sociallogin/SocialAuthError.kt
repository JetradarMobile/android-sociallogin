package com.jetradarmobile.sociallogin

open class SocialAuthError(val reason: Reason, cause: Throwable? = null) : Throwable(reason.message, cause) {
  constructor(message: String) : this(Reason(message))

  object CANCELLED : SocialAuthError(Reason.CANCEL)
  object EMPTY : SocialAuthError(Reason.EMPTY)
  object UNKNOWN : SocialAuthError(Reason.UNKNOWN)

  open class Reason(val message: String) {
    object CANCEL : Reason("Authorization process was cancelled")
    object EMPTY : Reason("Auth token is null or empty")
    object UNKNOWN : Reason("Unknown auth error")
  }
}