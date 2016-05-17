package com.malliina.push.wns

/**
  * @param description a user-friendly message
  * @see https://msdn.microsoft.com/library/windows/apps/hh465435.aspx#WNSResponseCodes
  */
sealed abstract class WNSError(val reason: String, val description: String)

object WNSError {
  def fromStatusCode(code: Int): PartialFunction[Int, WNSError] = {
    case 400 => IncorrectHeaders
    case 401 => InvalidAuthentication
    case 403 => ForbiddenURI
    case 404 => InvalidURI
    case 405 => InvalidMethod
    case 406 => ThrottleExceeded
    case 410 => ChannelExpired
    case 413 => PayloadSizeExceeded
    case 500 => DeliveryError
    case 503 => ServiceUnavailable
  }
}

case object IncorrectHeaders
  extends WNSError("BadRequest", "One or more headers were specified incorrectly or conflict with another header.")

case object InvalidAuthentication
  extends WNSError("BadRequest", "The cloud service did not present a valid authentication ticket. The OAuth ticket may be invalid.")

case object ForbiddenURI
  extends WNSError("Forbidden", "The cloud service is not authorized to send a notification to this URI even though they are authenticated.")

case object InvalidURI
  extends WNSError("NotFound", "The channel URI is not valid or is not recognized by WNS.")

case object InvalidMethod
  extends WNSError("MethodNotAllowed", "Invalid method (GET, CREATE); only POST (Windows or Windows Phone) or DELETE (Windows Phone only) is allowed.")

case object ThrottleExceeded
  extends WNSError("NotAcceptable", "The cloud service exceeded its throttle limit.")

case object ChannelExpired
  extends WNSError("Gone", "The channel expired.")

case object PayloadSizeExceeded
  extends WNSError("RequestEntityTooLarge", "The notification payload exceeds the 5000 byte size limit.")

case object DeliveryError
  extends WNSError("InternalServerError", "An internal failure caused notification delivery to fail.")

case object ServiceUnavailable
  extends WNSError("ServiceUnavailable", "The server is currently unavailable.")
