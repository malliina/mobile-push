package com.mle.push.gcm

import com.mle.push.gcm.GCMResult.GCMResultError
import play.api.libs.json.{JsResult, JsValue, Json, Reads}

/**
 * @author Michael
 */
case class GCMResult(message_id: Option[String], registration_id: Option[String], error: Option[GCMResult.GCMResultError])

object GCMResult {

  sealed trait GCMResultError

  case object MissingRegistration extends GCMResultError

  case object InvalidRegistration extends GCMResultError

  case object MismatchSenderId extends GCMResultError

  case object NotRegistered extends GCMResultError

  case object MessageTooBig extends GCMResultError

  case object InvalidDataKey extends GCMResultError

  case object InvalidTtl extends GCMResultError

  case object Unavailable extends GCMResultError

  case object InternalServerError extends GCMResultError

  case object InvalidPackageName extends GCMResultError

  case object DeviceMessageRateExceeded extends GCMResultError

  case class UnknownError(name: String) extends GCMResultError

  implicit val errorJson: Reads[GCMResultError] = new Reads[GCMResultError] {
    override def reads(json: JsValue): JsResult[GCMResultError] = json.validate[String].map {
      case "MissingRegistration" => MissingRegistration
      case "InvalidRegistration" => InvalidRegistration
      case "MismatchSenderId" => MismatchSenderId
      case "NotRegistered" => NotRegistered
      case "MessageTooBig" => MessageTooBig
      case "InvalidDataKey" => InvalidDataKey
      case "InvalidTtl" => InvalidTtl
      case "Unavailable" => Unavailable
      case "InternalServerError" => InternalServerError
      case "InvalidPackageName" => InvalidPackageName
      case "DeviceMessageRateExceeded" => DeviceMessageRateExceeded
      case other => UnknownError(other)
    }
  }
  implicit val json = Json.reads[GCMResult]
}
