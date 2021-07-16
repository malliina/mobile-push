package com.malliina.push.android

import com.malliina.json.PrimitiveFormats
import io.circe.Codec
import io.circe.generic.semiauto._

import scala.concurrent.duration.Duration

case class AndroidMessage(data: Map[String, String], expiresAfter: Duration)

object AndroidMessage {
  implicit val duration: Codec[Duration] = PrimitiveFormats.durationCodec
  implicit val json: Codec[AndroidMessage] = deriveCodec[AndroidMessage]
}
