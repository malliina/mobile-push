package com.malliina.push.adm

import com.malliina.json.PrimitiveFormats
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

import scala.concurrent.duration.Duration

case class AccessToken(
  access_token: String,
  expires_in: Duration,
  scope: String,
  token_type: String
)

object AccessToken:
  given dc: Codec[Duration] = PrimitiveFormats.durationCodec
  given json: Codec[AccessToken] = deriveCodec[AccessToken]
