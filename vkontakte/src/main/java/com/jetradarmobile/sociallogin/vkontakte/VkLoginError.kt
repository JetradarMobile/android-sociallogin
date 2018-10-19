package com.jetradarmobile.sociallogin.vkontakte

import com.jetradarmobile.sociallogin.SocialLoginError

class VkLoginError(reason: Reason) : SocialLoginError(reason) {
  object NoLogin : Reason("Vk token receiving error")
}