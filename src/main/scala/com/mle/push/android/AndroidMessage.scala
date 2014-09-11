package com.mle.push.android

import com.mle.json.JsonFormats
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

/**
 * @author Michael
 */
case class AndroidMessage(data: Map[String, String], expiresAfter: Duration)

object AndroidMessage {
  implicit val durationFormat = JsonFormats.durationFormat
  implicit val json = Json.format[AndroidMessage]
}