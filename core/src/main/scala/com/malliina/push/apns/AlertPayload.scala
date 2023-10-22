package com.malliina.push.apns

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class AlertPayload(
  body: String,
  title: Option[String] = None,
  subtitle: Option[String] = None,
  launchImage: Option[String] = None,
  actionLocKey: Option[String] = None,
  locKey: Option[String] = None,
  locArgs: Option[Seq[String]] = None,
  titleLocKey: Option[String] = None,
  titleLocArgs: Option[Seq[String]] = None,
  subtitleLocKey: Option[String] = None,
  subtitleLocArgs: Option[Seq[String]] = None
)

object AlertPayload {
  case class AlertPayloadJson(
    body: String,
    title: Option[String],
    subtitle: Option[String],
    `launch-image`: Option[String],
    `action-loc-key`: Option[String],
    `loc-key`: Option[String],
    `loc-args`: Option[Seq[String]],
    `title-loc-key`: Option[String],
    `title-loc-args`: Option[Seq[String]],
    `subtitle-loc-key`: Option[String],
    `subtitle-loc-args`: Option[Seq[String]]
  ) {
    def toPayload: AlertPayload = AlertPayload(
      body,
      title,
      subtitle,
      `launch-image`,
      `action-loc-key`,
      `loc-key`,
      `loc-args`,
      `title-loc-key`,
      `title-loc-args`,
      `subtitle-loc-key`,
      `subtitle-loc-args`
    )
  }
  def to(json: AlertPayload): AlertPayloadJson = AlertPayloadJson(
    json.body,
    json.title,
    json.subtitle,
    json.launchImage,
    json.actionLocKey,
    json.locKey,
    json.locArgs,
    json.titleLocKey,
    json.titleLocArgs,
    json.subtitleLocKey,
    json.subtitleLocArgs
  )
  val rawDecoder: Decoder[AlertPayloadJson] = deriveDecoder[AlertPayloadJson]
  val rawEncoder: Encoder[AlertPayloadJson] = deriveEncoder[AlertPayloadJson]
  implicit val json: Codec[AlertPayload] = Codec.from(
    rawDecoder.map(raw => raw.toPayload),
    rawEncoder.contramap(AlertPayload.to)
  )
}
