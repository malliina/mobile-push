package com.malliina.push.android

import com.malliina.json.PrimitiveFormats
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

import scala.concurrent.duration.Duration

case class AndroidMessage(data: Map[String, String], expiresAfter: Duration)

object AndroidMessage:
  given duration: Codec[Duration] = PrimitiveFormats.durationCodec
  given json: Codec[AndroidMessage] = deriveCodec[AndroidMessage]
