package com.malliina.push.gcm

import com.malliina.json.JsonFormats
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

/**
  *
  * @author mle
  */
case class GCMLetter(registration_ids: Seq[GCMToken],
                     data: Map[String, String],
                     time_to_live: Option[Duration] = None,
                     collapse_key: Option[String] = None,
                     delay_while_idle: Option[Boolean] = None,
                     restricted_package_name: Option[String] = None,
                     dry_run: Option[Boolean] = None)

object GCMLetter {
  implicit val durationFormat = JsonFormats.durationFormat
  implicit val format = Json.format[GCMLetter]
}
