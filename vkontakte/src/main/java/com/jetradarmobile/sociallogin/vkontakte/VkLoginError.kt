package com.jetradarmobile.sociallogin.vkontakte

import com.jetradarmobile.sociallogin.SocialAuthError

class VkLoginError(reason: Reason) : SocialAuthError(reason) {
  object NoLogin : Reason("Vk token receiving error")
}