package com.malliina.push.apns

import com.malliina.json.JsonEnum

/**
  * @author mle
  * @see https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/APNsProviderAPI.html Table 6.6
  */
sealed abstract class ErrorReason(val reason: String, val description: String)

object ErrorReason extends JsonEnum[ErrorReason] {
  val ReasonKey = "reason"
  override val all: Seq[ErrorReason] = Seq(
    PayloadEmpty, PayloadTooLarge, BadTopic,
    TopicDisallowed, BadMessageId, BadExpirationDate,
    BadPriority, MissingDeviceToken, BadDeviceToken,
    DeviceTokenNotForTopic, Unregistered, DuplicateHeaders,
    BadCertificateEnvironment, BadCertificate, Forbidden,
    BadPath, MethodNotAllowed, TooManyRequests,
    IdleTimeout, Shutdown, InternalServerError,
    ServiceUnavailable, MissingTopic)

  override def resolveName(item: ErrorReason): String = item.reason
}

case object PayloadEmpty extends ErrorReason("PayloadEmpty", "The message payload was empty.")

case object PayloadTooLarge extends ErrorReason("PayloadTooLarge", "The message payload was too large. The maximum payload size is 4096 bytes.")

case object BadTopic extends ErrorReason("BadTopic", "The apns-topic was invalid.")

case object TopicDisallowed extends ErrorReason("TopicDisallowed", "Pushing to this topic is not allowed.")

case object BadMessageId extends ErrorReason("BadMessageId", "The apns-id value is bad.")

case object BadExpirationDate extends ErrorReason("BadExpirationDate", "The apns-expiration value is bad.")

case object BadPriority extends ErrorReason("BadPriority", "The apns-priority value is bad.")

case object MissingDeviceToken extends ErrorReason("MissingDeviceToken", "The device token is not specified in the request :path. Verify that the :path header contains the device token.")

case object BadDeviceToken extends ErrorReason("BadDeviceToken", "The specified device token was bad. Verify that the request contains a valid token and that the token matches the environment.")

case object DeviceTokenNotForTopic extends ErrorReason("DeviceTokenNotForTopic", "The device token does not match the specified topic.")

case object Unregistered extends ErrorReason("Unregistered", "The device token is inactive for the specified topic.")

case object DuplicateHeaders extends ErrorReason("DuplicateHeaders", "One or more headers were repeated.")

case object BadCertificateEnvironment extends ErrorReason("BadCertificateEnvironment", "The client certificate was for the wrong environment.")

case object BadCertificate extends ErrorReason("BadCertificate", "The certificate was bad.")

case object Forbidden extends ErrorReason("Forbidden", "The specified action is not allowed.")

case object BadPath extends ErrorReason("BadPath", "The request contained a bad :path value.")

case object MethodNotAllowed extends ErrorReason("MethodNotAllowed", "The specified :method was not POST.")

case object TooManyRequests extends ErrorReason("TooManyRequests", "Too many requests were made consecutively to the same device token.")

case object IdleTimeout extends ErrorReason("IdleTimeout", "Idle time out.")

case object Shutdown extends ErrorReason("Shutdown", "The server is shutting down.")

case object InternalServerError extends ErrorReason("InternalServerError", "An internal server error occurred.")

case object ServiceUnavailable extends ErrorReason("ServiceUnavailable", "The service is unavailable.")

case object MissingTopic extends ErrorReason("MissingTopic", "The apns-topic header of the request was not specified and was required. The apns-topic header is mandatory when the client is connected using a certificate that supports multiple topics.")

case object UnknownReason extends ErrorReason("UnknownReason", "An unknown error occurred.")