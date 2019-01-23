package com.jetradarmobile.sociallogin.google

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jetradarmobile.sociallogin.SocialAccount
import com.jetradarmobile.sociallogin.SocialAuthCallback
import com.jetradarmobile.sociallogin.SocialNetwork
import com.jetradarmobile.sociallogin.google.GoogleLoginError.EmptyAccount
import com.jetradarmobile.sociallogin.google.GoogleLoginError.LogoutCancelled
import com.jetradarmobile.sociallogin.google.GoogleLoginError.LogoutFailed


class GoogleNetwork(private val clientId: String) : SocialNetwork {
  private lateinit var googleSignInClient: GoogleSignInClient

  private var loginCallback: SocialAuthCallback? = null
  override val code: String = CODE

  override fun login(fragment: Fragment, callback: SocialAuthCallback) {
    loginCallback = callback
    createClient(fragment)
    if (GoogleSignIn.getLastSignedInAccount(fragment.requireContext()) != null) {
      // drop old account to prevent receiving of non valid token
      googleSignInClient.signOut()
          .continueWithTask { googleSignInClient.revokeAccess() }
          .addOnCompleteListener { openLoginScreen(fragment) }
          .addOnCanceledListener { callback.onAuthError(this, GoogleLoginError(LogoutCancelled)) }
          .addOnFailureListener { callback.onAuthError(this, GoogleLoginError(LogoutFailed)) }
    } else {
      openLoginScreen(fragment)
    }
  }

  private fun openLoginScreen(fragment: Fragment) = fragment.startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE)

  override fun logout(fragment: Fragment, callback: SocialAuthCallback) {
    createClient(fragment)
    googleSignInClient.signOut()
        .continueWithTask { googleSignInClient.revokeAccess() }
        .addOnSuccessListener { callback.onLogoutSuccess(this) }
        .addOnCanceledListener { callback.onAuthError(this, GoogleLoginError(LogoutCancelled)) }
        .addOnFailureListener { callback.onAuthError(this, GoogleLoginError(LogoutFailed)) }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      handleSignInResult(task)
    }
  }

  private fun createClient(fragment: Fragment) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestId()
        .requestEmail()
        .requestServerAuthCode(clientId)
        .requestIdToken(clientId)
        .build()

    googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso)
  }

  private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
    try {
      val account = task.getResult(ApiException::class.java)
      if (account != null) loginCallback?.onLoginSuccess(this, createSocialToken(account))
      else loginCallback?.onAuthError(this, GoogleLoginError(EmptyAccount))
    } catch (e: ApiException) {
      loginCallback?.onAuthError(this, GoogleLoginError.byCode(e))
    }
  }

  private fun createSocialToken(account: GoogleSignInAccount) = SocialAccount(
      token = account.idToken ?: "",
      networkCode = CODE,
      userId = account.id ?: "",
      userName = account.displayName ?: "",
      email = account.email ?: ""
  )

  companion object {
    private const val REQUEST_CODE = 0x001b
    const val CODE = "google"
  }
}