package com.jetradarmobile.sociallogin

import android.content.Intent
import androidx.fragment.app.Fragment

interface SocialNetwork {
  val code: String

  fun login(fragment: Fragment, callback: SocialAuthCallback)
  fun logout(fragment: Fragment, callback: SocialAuthCallback)

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}