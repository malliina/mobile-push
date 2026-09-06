package com.malliina.push.wns

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class WNSAccessToken(access_token: String, token_type: String)

object WNSAccessToken:
  implicit val json: Codec[WNSAccessToken] = deriveCodec[WNSAccessToken]
