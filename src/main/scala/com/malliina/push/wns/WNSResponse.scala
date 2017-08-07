package com.malliina.push.wns

import com.malliina.http.WebResponse

/**
  * @param description a user-friendly message
  * @see https://msdn.microsoft.com/library/windows/apps/hh465435.aspx#WNSResponseCodes
  */
sealed abstract class WNSResponse(val reason: String, val description: String) {
  def response: WebResponse

  def statusCode: Int = response.code

  def isSuccess = statusCode == 200
}

object WNSResponse {
  def fromResponse(response: WebResponse): WNSResponse = {
    response.code match {
      case 200 => WNSSuccess(response)
      case 400 => IncorrectHeaders(response)
      case 401 => InvalidAuthentication(response)
      case 403 => ForbiddenURI(response)
      case 404 => InvalidURI(response)
      case 405 => InvalidMethod(response)
      case 406 => ThrottleExceeded(response)
      case 410 => ChannelExpired(response)
      case 413 => PayloadSizeExceeded(response)
      case 500 => DeliveryError(response)
      case 503 => ServiceUnavailable(response)
      case other if other >= 400 => UnknownError(response)
    }
  }

  case class WNSSuccess(response: WebResponse)
    extends WNSResponse("Success", "Received")

  case class IncorrectHeaders(response: WebResponse)
    extends WNSResponse("BadRequest", "One or more headers were specified incorrectly or conflict with another header.")

  case class InvalidAuthentication(response: WebResponse)
    extends WNSResponse("BadRequest", "The cloud service did not present a valid authentication ticket. The OAuth ticket may be invalid.")

  case class ForbiddenURI(response: WebResponse)
    extends WNSResponse("Forbidden", "The cloud service is not authorized to send a notification to this URI even though they are authenticated.")

  case class InvalidURI(response: WebResponse)
    extends WNSResponse("NotFound", "The channel URI is not valid or is not recognized by WNS.")

  case class InvalidMethod(response: WebResponse)
    extends WNSResponse("MethodNotAllowed", "Invalid method (GET, CREATE); only POST (Windows or Windows Phone) or DELETE (Windows Phone only) is allowed.")

  case class ThrottleExceeded(response: WebResponse)
    extends WNSResponse("NotAcceptable", "The cloud service exceeded its throttle limit.")

  case class ChannelExpired(response: WebResponse)
    extends WNSResponse("Gone", "The channel expired.")

  case class PayloadSizeExceeded(response: WebResponse)
    extends WNSResponse("RequestEntityTooLarge", "The notification payload exceeds the 5000 byte size limit.")

  case class DeliveryError(response: WebResponse)
    extends WNSResponse("InternalServerError", "An internal failure caused notification delivery to fail.")

  case class ServiceUnavailable(response: WebResponse)
    extends WNSResponse("ServiceUnavailable", "The server is currently unavailable.")

  case class UnknownError(response: WebResponse)
    extends WNSResponse("UnknownError", s"Unrecognized status code: ${response.code}")

}
