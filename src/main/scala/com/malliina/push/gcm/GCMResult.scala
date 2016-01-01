package com.malliina.push.gcm

import play.api.libs.json._

/**
 * @author Michael
 */
case class GCMResult(message_id: Option[String], registration_id: Option[String], error: Option[GCMResult.GCMResultError])

object GCMResult {

  sealed trait GCMResultError {
    def name: String
  }

  case object MissingRegistration extends GCMResultError {
    override def name: String = "MissingRegistration"
  }

  case object InvalidRegistration extends GCMResultError {
    override def name: String = "InvalidRegistration"
  }

  case object MismatchSenderId extends GCMResultError {
    override def name: String = "MismatchSenderId"
  }

  case object NotRegistered extends GCMResultError {
    override def name: String = "NotRegistered"
  }

  case object MessageTooBig extends GCMResultError {
    override def name: String = "MessageTooBig"
  }

  case object InvalidDataKey extends GCMResultError {
    override def name: String = "InvalidDataKey"
  }

  case object InvalidTtl extends GCMResultError {
    override def name: String = "InvalidTtl"
  }

  case object Unavailable extends GCMResultError {
    override def name: String = "Unavailable"
  }

  case object InternalServerError extends GCMResultError {
    override def name: String = "InternalServerError"
  }

  case object InvalidPackageName extends GCMResultError {
    override def name: String = "InvalidPackageName"
  }

  case object DeviceMessageRateExceeded extends GCMResultError {
    override def name: String = "DeviceMessageRateExceeded"
  }

  case class UnknownError(name: String) extends GCMResultError

  val knownErrors = Seq(
    MissingRegistration, InvalidRegistration, MismatchSenderId,
    NotRegistered, MessageTooBig, InvalidDataKey,
    InvalidTtl, Unavailable, InternalServerError,
    InvalidPackageName, DeviceMessageRateExceeded)

  implicit val errorJson: Format[GCMResultError] = new Format[GCMResultError] {
    override def writes(o: GCMResultError): JsValue = Json.toJson(o.name)

    override def reads(json: JsValue): JsResult[GCMResultError] = {
      json.validate[String].map(name => knownErrors.find(_.name == name) getOrElse UnknownError(name))
    }
  }
  implicit val json = Json.format[GCMResult]
}
