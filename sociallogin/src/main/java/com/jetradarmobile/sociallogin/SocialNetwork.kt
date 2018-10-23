package com.jetradarmobile.sociallogin

import android.app.Activity
import android.content.Intent

interface SocialNetwork {
  val code: String
  val requestCode: Int

  fun login(activity: Activity, callback: SocialLoginCallback)
  fun logout(activity: Activity)

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}