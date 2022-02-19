package com.malliina.push.gcm

import com.malliina.push.json.OpenEnum
import io.circe.Codec
import io.circe.generic.semiauto._

sealed abstract class GCMResultError(val name: String)

object GCMResultError extends OpenEnum[GCMResultError] {
  override val all: Seq[GCMResultError] = Seq(
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

  override def resolveName(item: GCMResultError): String = item.name

  override def default(name: String): GCMResultError = UnknownError(name)
}

case class GCMResult(
  message_id: Option[String],
  registration_id: Option[String],
  error: Option[GCMResultError]
)

object GCMResult {
  implicit val json: Codec[GCMResult] = deriveCodec[GCMResult]
}
