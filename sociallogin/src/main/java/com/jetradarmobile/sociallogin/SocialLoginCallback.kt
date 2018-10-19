package com.jetradarmobile.sociallogin

/**
 *  Callback interface. Handle login result
 */
interface SocialLoginCallback {

  /**
   * Calls when login was successful
   *
   * @param socialNetwork [SocialNetwork] implementation in which login was requested
   * @param account [SocialAccount] authorization token and some user data
   */
  fun onLoginSuccess(socialNetwork: SocialNetwork, account: SocialAccount)

  /**
   * Calls when some error occurred
   *
   * @param socialNetwork [SocialNetwork] implementation with which request was unsuccessful
   * @param error [SocialLoginError] error a social login error
   */
  fun onLoginError(socialNetwork: SocialNetwork, error: SocialLoginError)
}