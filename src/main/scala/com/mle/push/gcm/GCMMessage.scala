package com.mle.push.gcm

import com.mle.json.JsonFormats
import play.api.libs.json.Json

import scala.concurrent.duration.{Duration, DurationInt}

/**
 *
 * @author mle
 */
case class GCMMessage(registration_ids: Seq[String], data: Map[String, String], time_to_live: Duration = 20.seconds)

object GCMMessage {

  implicit val durationFormat = JsonFormats.durationFormat

  implicit val format = Json.format[GCMMessage]
}
