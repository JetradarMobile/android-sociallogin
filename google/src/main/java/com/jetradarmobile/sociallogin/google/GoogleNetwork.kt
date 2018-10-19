package com.jetradarmobile.sociallogin.google

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jetradarmobile.sociallogin.SocialLoginCallback
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.google.GoogleLoginError.EmptyAccount
import com.jetradarmobile.sociallogin.google.GoogleLoginError.LogoutCancelled
import com.jetradarmobile.sociallogin.google.GoogleLoginError.LogoutFailed


class GoogleNetwork(private val clientId: String) : SocialNetwork {
  private lateinit var googleSignInClient: GoogleSignInClient
  private var loginCallback: SocialLoginCallback? = null

  override val code: String = CODE

  override fun login(activity: Activity, callback: SocialLoginCallback) {
    loginCallback = callback
    createClient(activity)
    if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
      // drop old account to prevent receiving of non valid token
      googleSignInClient.signOut()
          .continueWithTask { googleSignInClient.revokeAccess() }
          .addOnCompleteListener { openLoginScreen(activity) }
          .addOnCanceledListener { callback.onLoginError(this, GoogleLoginError(LogoutCancelled)) }
          .addOnFailureListener { callback.onLoginError(this, GoogleLoginError(LogoutFailed)) }
    } else {
      openLoginScreen(activity)
    }
  }

  private fun openLoginScreen(activity: Activity) {
    val signInIntent = googleSignInClient.signInIntent
    activity.startActivityForResult(signInIntent, REQUEST_CODE)
  }

  override fun logout(activity: Activity) {
    createClient(activity)
    googleSignInClient.signOut()
        .continueWithTask { googleSignInClient.revokeAccess() }
        .addOnCompleteListener { openLoginScreen(activity) }
        .addOnCanceledListener { loginCallback?.onLoginError(this, GoogleLoginError(LogoutCancelled)) }
        .addOnFailureListener { loginCallback?.onLoginError(this, GoogleLoginError(LogoutFailed)) }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      handleSignInResult(task)
    }
  }

  private fun createClient(activity: Activity) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestId()
        .requestEmail()
        .requestServerAuthCode(clientId)
        .requestIdToken(clientId)
        .build()

    googleSignInClient = GoogleSignIn.getClient(activity, gso)
  }

  private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
    try {
      val account = task.getResult(ApiException::class.java)
      if (account != null) loginCallback?.onLoginSuccess(this, createSocialToken(account))
      else loginCallback?.onLoginError(this, GoogleLoginError(EmptyAccount))
    } catch (e: ApiException) {
      loginCallback?.onLoginError(this, GoogleLoginError.byCode(e))
    }
  }

  private fun createSocialToken(account: GoogleSignInAccount) = SocialAccount(
      token = account.idToken ?: "",
      userId = account.id ?: "",
      userName = account.displayName ?: "",
      email = account.email ?: ""
  )

  companion object {
    private const val REQUEST_CODE = 0x0C1E
    const val CODE = "google"
  }
}