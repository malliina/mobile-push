package com.malliina.push.wns

import io.circe._
import io.circe.generic.semiauto._

case class WNSAccessToken(access_token: String, token_type: String)

object WNSAccessToken {
  implicit val json: Codec[WNSAccessToken] = deriveCodec[WNSAccessToken]
}
