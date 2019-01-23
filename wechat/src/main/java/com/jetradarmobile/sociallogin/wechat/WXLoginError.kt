package com.jetradarmobile.sociallogin.wechat

import com.jetradarmobile.sociallogin.SocialAuthError

class WXLoginError(reason: Reason, cause: Throwable? = null) : SocialAuthError(reason, cause) {
  object DENY : Reason("Operation denied")
  object UNSUPPORTED : Reason("Operation unsupported")
  object UNKNOWN : Reason("Unknown error")
}