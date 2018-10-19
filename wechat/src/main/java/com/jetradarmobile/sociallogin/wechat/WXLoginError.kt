package com.jetradarmobile.sociallogin.wechat

import com.jetradarmobile.sociallogin.SocialLoginError

class WXLoginError(reason: Reason, cause: Throwable? = null) : SocialLoginError(reason, cause) {
  object DENY : Reason("Operation denied")
  object UNSUPPORTED : Reason("Operation unsupported")
  object UNKNOWN : Reason("Unknown error")
}