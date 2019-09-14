package com.malliina.push.gcm

import play.api.libs.json._

case class GCMResult(message_id: Option[String],
                     registration_id: Option[String],
                     error: Option[GCMResult.GCMResultError])

object GCMResult {

  sealed abstract class GCMResultError(val name: String)

  case object MissingRegistration extends GCMResultError("MissingRegistration")
  case object InvalidRegistration extends GCMResultError("InvalidRegistration")
  case object MismatchSenderId extends GCMResultError("MismatchSenderId")
  case object NotRegistered extends GCMResultError("NotRegistered")
  case object MessageTooBig extends GCMResultError("MessageTooBig")
  case object InvalidDataKey extends GCMResultError("InvalidDataKey")
  case object InvalidTtl extends GCMResultError("InvalidTtl")
  case object Unavailable extends GCMResultError("Unavailable")
  case object InternalServerError extends GCMResultError("InternalServerError")
  case object InvalidPackageName extends GCMResultError("InvalidPackageName")
  case object DeviceMessageRateExceeded extends GCMResultError("DeviceMessageRateExceeded")

  case class UnknownError(n: String) extends GCMResultError(n)

  val knownErrors = Seq(
    MissingRegistration,
    InvalidRegistration,
    MismatchSenderId,
    NotRegistered,
    MessageTooBig,
    InvalidDataKey,
    InvalidTtl,
    Unavailable,
    InternalServerError,
    InvalidPackageName,
    DeviceMessageRateExceeded
  )

  implicit val errorJson: Format[GCMResultError] = new Format[GCMResultError] {
    override def writes(o: GCMResultError): JsValue = Json.toJson(o.name)

    override def reads(json: JsValue): JsResult[GCMResultError] = {
      json
        .validate[String]
        .map(name => knownErrors.find(_.name == name) getOrElse UnknownError(name))
    }
  }
  implicit val json = Json.format[GCMResult]
}
