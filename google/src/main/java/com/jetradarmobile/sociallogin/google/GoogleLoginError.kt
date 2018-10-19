package com.jetradarmobile.sociallogin.google


import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.jetradarmobile.sociallogin.SocialLoginError


open class GoogleLoginError(reason: Reason, cause: Throwable? = null) : SocialLoginError(reason, cause) {

  object NoLogin : Reason("Google account receive error")
  object SignInRequired : Reason("SignInRequired")
  object InvalidAccount : Reason("InvalidAccount")
  object ResolutionRequired : Reason("ResolutionRequired")
  object NetworkError : Reason("NetworkError")
  object InternalError : Reason("InternalError")
  object DeveloperError : Reason("DeveloperError")
  object Error : Reason("Error")
  object Interrupted : Reason("Interrupted")
  object Timeout : Reason("Timeout")
  object ApiNotConnected : Reason("ApiNotConnected")
  object EmptyAccount : Reason("EmptyAccount")
  object LogoutCancelled : Reason("LogoutCancelled")
  object LogoutFailed : Reason("LogoutFailed")

  companion object Factory {

    fun byCode(cause: ApiException): GoogleLoginError = GoogleLoginError(when (cause.statusCode) {
      GoogleSignInStatusCodes.SIGN_IN_REQUIRED    -> SignInRequired
      GoogleSignInStatusCodes.INVALID_ACCOUNT     -> InvalidAccount
      GoogleSignInStatusCodes.RESOLUTION_REQUIRED -> ResolutionRequired
      GoogleSignInStatusCodes.NETWORK_ERROR       -> NetworkError
      GoogleSignInStatusCodes.INTERNAL_ERROR      -> InternalError
      GoogleSignInStatusCodes.DEVELOPER_ERROR     -> DeveloperError
      GoogleSignInStatusCodes.ERROR               -> Error
      GoogleSignInStatusCodes.INTERRUPTED         -> Interrupted
      GoogleSignInStatusCodes.TIMEOUT             -> Timeout
      GoogleSignInStatusCodes.SIGN_IN_CANCELLED   -> Reason.CANCEL
      GoogleSignInStatusCodes.CANCELED            -> Reason.CANCEL
      GoogleSignInStatusCodes.API_NOT_CONNECTED   -> ApiNotConnected
      else                                        -> NoLogin
    }, cause)
  }
}
