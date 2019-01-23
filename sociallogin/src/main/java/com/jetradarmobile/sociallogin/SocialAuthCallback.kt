package com.jetradarmobile.sociallogin

/**
 *  Callback interface. Handle login result
 */
interface SocialAuthCallback {

  /**
   * Calls when login was successful
   *
   * @param socialNetwork [SocialNetwork] implementation in which login was requested
   * @param account [SocialAccount] authorization token and some user data
   */
  fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount)

  /**
   * Calls when logout was successful
   *
   * @param socialNetwork [SocialNetwork] implementation in which login was requested
   */
  fun onLogoutSuccess(socialNetwork: SocialNetwork)

  /**
   * Calls when some error occurred
   *
   * @param socialNetwork [SocialNetwork] implementation with which request was unsuccessful
   * @param error [SocialAuthError] error a social login error
   */
  fun onAuthError(socialNetwork: SocialNetwork, error: SocialAuthError)
}