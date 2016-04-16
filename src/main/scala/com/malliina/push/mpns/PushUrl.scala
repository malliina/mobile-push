package com.malliina.push.mpns

import play.api.libs.json.Json

case class PushUrl(url: MPNSToken, silent: Boolean, tag: String)

object PushUrl {
  implicit val json = Json.format[PushUrl]
}
