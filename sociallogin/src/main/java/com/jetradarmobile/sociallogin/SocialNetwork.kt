package com.jetradarmobile.sociallogin

import android.app.Activity
import android.content.Intent

interface SocialNetwork {
  val code: String

  fun login(activity: Activity, callback: SocialAuthCallback)
  fun logout(activity: Activity, callback: SocialAuthCallback)

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}