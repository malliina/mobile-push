package com.malliina.push.apns

import io.circe._
import io.circe.generic.semiauto._

case class AlertPayload(
  body: String,
  title: Option[String] = None,
  launchImage: Option[String] = None,
  actionLocKey: Option[String] = None,
  locKey: Option[String] = None,
  locArgs: Option[Seq[String]] = None,
  titleLocKey: Option[String] = None,
  titleLocArgs: Option[Seq[String]] = None
)

object AlertPayload {
  case class AlertPayloadJson(
    body: String,
    title: Option[String],
    `launch-image`: Option[String],
    `action-loc-key`: Option[String],
    `loc-key`: Option[String],
    `loc-args`: Option[Seq[String]],
    `title-loc-key`: Option[String],
    `title-loc-args`: Option[Seq[String]]
  ) {
    def toPayload: AlertPayload = AlertPayload(
      body,
      title,
      `launch-image`,
      `action-loc-key`,
      `loc-key`,
      `loc-args`,
      `title-loc-key`,
      `title-loc-args`
    )
  }
  def to(json: AlertPayload): AlertPayloadJson = AlertPayloadJson(
    json.body,
    json.title,
    json.launchImage,
    json.actionLocKey,
    json.locKey,
    json.locArgs,
    json.titleLocKey,
    json.titleLocArgs
  )
  val rawDecoder: Decoder[AlertPayloadJson] = deriveDecoder[AlertPayloadJson]
  val rawEncoder: Encoder[AlertPayloadJson] = deriveEncoder[AlertPayloadJson]
  implicit val json: Codec[AlertPayload] = Codec.from(
    rawDecoder.map(raw => raw.toPayload),
    rawEncoder.contramap(AlertPayload.to)
  )
}
