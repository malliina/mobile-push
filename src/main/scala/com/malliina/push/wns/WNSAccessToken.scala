package com.malliina.push.wns

import play.api.libs.json.Json

case class WNSAccessToken(access_token: String, token_type: String)

object WNSAccessToken {
  implicit val json = Json.format[WNSAccessToken]
}
