package com.jetradarmobile.sociallogin.facebook

import com.jetradarmobile.sociallogin.SocialAuthError

class FacebookLoginError(reason: Reason) : SocialAuthError(reason) {
  constructor(message: String) : this(Reason(message))

  object NoLogin : Reason("No facebook login token present")
}