package com.mle.push.mpns

import play.api.libs.json.Json

/**
 *
 * @author mle
 */
case class PushUrl(url: String, silent: Boolean, tag: String)

object PushUrl {
  implicit val json = Json.format[PushUrl]
}