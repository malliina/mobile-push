package com.malliina.push.apns

import com.malliina.push.json.OpenEnum
import io.circe._
import io.circe.generic.semiauto._

/** @see https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/APNsProviderAPI.html Table 6.6
  */
sealed abstract class APNSError(val reason: String, val description: String)

object APNSError extends OpenEnum[APNSError] {
  val ReasonKey = "reason"
  override val all: Seq[APNSError] = Seq(
    PayloadEmpty,
    PayloadTooLarge,
    BadTopic,
    TopicDisallowed,
    BadMessageId,
    BadExpirationDate,
    BadPriority,
    MissingDeviceToken,
    BadDeviceToken,
    DeviceTokenNotForTopic,
    Unregistered,
    DuplicateHeaders,
    BadCertificateEnvironment,
    BadCertificate,
    Forbidden,
    BadPath,
    MethodNotAllowed,
    TooManyRequests,
    IdleTimeout,
    Shutdown,
    InternalServerError,
    ServiceUnavailable,
    MissingTopic,
    TooManyProviderTokenUpdates,
    BadCollapseId,
    ExpiredProviderToken,
    InvalidProviderToken,
    MissingProviderToken
  )

  override def default(name: String): APNSError = OtherReason(name)

  override def resolveName(item: APNSError): String = item.reason
}

case object PayloadEmpty extends APNSError("PayloadEmpty", "The message payload was empty.")
case object PayloadTooLarge
  extends APNSError(
    "PayloadTooLarge",
    "The message payload was too large. The maximum payload size is 4096 bytes."
  )
case object BadTopic extends APNSError("BadTopic", "The apns-topic was invalid.")
case object TopicDisallowed
  extends APNSError("TopicDisallowed", "Pushing to this topic is not allowed.")
case object BadMessageId extends APNSError("BadMessageId", "The apns-id value is bad.")
case object BadExpirationDate
  extends APNSError("BadExpirationDate", "The apns-expiration value is bad.")
case object BadPriority extends APNSError("BadPriority", "The apns-priority value is bad.")
case object MissingDeviceToken
  extends APNSError(
    "MissingDeviceToken",
    "The device token is not specified in the request :path. Verify that the :path header contains the device token."
  )
case object BadDeviceToken
  extends APNSError(
    "BadDeviceToken",
    "The specified device token was bad. Verify that the request contains a valid token and that the token matches the environment."
  )
case object DeviceTokenNotForTopic
  extends APNSError(
    "DeviceTokenNotForTopic",
    "The device token does not match the specified topic."
  )
case object Unregistered
  extends APNSError("Unregistered", "The device token is inactive for the specified topic.")
case object DuplicateHeaders
  extends APNSError("DuplicateHeaders", "One or more headers were repeated.")
case object BadCertificateEnvironment
  extends APNSError(
    "BadCertificateEnvironment",
    "The client certificate was for the wrong environment."
  )
case object BadCertificate extends APNSError("BadCertificate", "The certificate was bad.")
case object Forbidden extends APNSError("Forbidden", "The specified action is not allowed.")
case object BadPath extends APNSError("BadPath", "The request contained a bad :path value.")
case object MethodNotAllowed
  extends APNSError("MethodNotAllowed", "The specified :method was not POST.")
case object TooManyRequests
  extends APNSError(
    "TooManyRequests",
    "Too many requests were made consecutively to the same device token."
  )
case object IdleTimeout extends APNSError("IdleTimeout", "Idle time out.")
case object Shutdown extends APNSError("Shutdown", "The server is shutting down.")
case object InternalServerError
  extends APNSError("InternalServerError", "An internal server error occurred.")
case object ServiceUnavailable
  extends APNSError("ServiceUnavailable", "The service is unavailable.")
case object MissingTopic
  extends APNSError(
    "MissingTopic",
    "The apns-topic header of the request was not specified and was required. The apns-topic header is mandatory when the client is connected using a certificate that supports multiple topics."
  )
case object TooManyProviderTokenUpdates
  extends APNSError("TooManyProviderTokenUpdates", "The provider token is being updated too often.")
case object BadCollapseId
  extends APNSError("BadCollapseId", "The collapse identifier exceeds the maximum allowed size.")
case object ExpiredProviderToken
  extends APNSError(
    "ExpiredProviderToken",
    "The provider token is stale and a new token should be generated."
  )
case object InvalidProviderToken
  extends APNSError(
    "InvalidProviderToken",
    "The provider token is not valid or the token signature could not be verified."
  )
case object MissingProviderToken
  extends APNSError(
    "MissingProviderToken",
    "No provider certificate was used to connect to APNs and Authorization header was missing or no provider token was specified."
  )
case class OtherReason(r: String) extends APNSError(r, "Generic error.")
case object UnknownReason extends APNSError("UnknownReason", "An unknown error occurred.")

case class APNSErrorJson(reason: APNSError)

object APNSErrorJson {
  implicit val json: Codec[APNSErrorJson] = deriveCodec[APNSErrorJson]
}
