package com.malliina.push.android

import com.malliina.json.JsonFormats
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

case class AndroidMessage(data: Map[String, String], expiresAfter: Duration)

object AndroidMessage {
  implicit val durationFormat = JsonFormats.durationFormat
  implicit val json = Json.format[AndroidMessage]
}
