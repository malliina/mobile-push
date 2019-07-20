package com.malliina.push.gcm

import com.malliina.json.PrimitiveFormats
import com.malliina.push.{Token, TokenCompanion}
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

case class GCMToken(token: String) extends AnyVal with Token

object GCMToken extends TokenCompanion[GCMToken] {
  type FCMToken = GCMToken
}

case class GCMResponse(multicast_id: Long,
                       success: Int,
                       failure: Int,
                       canonical_ids: Int,
                       results: Seq[GCMResult])

object GCMResponse {
  implicit val json = Json.reads[GCMResponse]
}

case class GCMLetter(registration_ids: Seq[GCMToken],
                     data: Map[String, String],
                     time_to_live: Option[Duration] = None,
                     collapse_key: Option[String] = None,
                     delay_while_idle: Option[Boolean] = None,
                     restricted_package_name: Option[String] = None,
                     dry_run: Option[Boolean] = None)

object GCMLetter {
  implicit val durationFormat = PrimitiveFormats.durationFormat
  implicit val format = Json.format[GCMLetter]
}

case class GCMMessage(data: Map[String, String],
                      expiresAfter: Option[Duration] = None,
                      collapseKey: Option[String] = None,
                      delayWhileIdle: Option[Boolean] = None,
                      restrictedPackageName: Option[String] = None,
                      dryRun: Option[Boolean] = None) {
  def toLetter(ids: Seq[GCMToken]) = GCMLetter(
    ids,
    data,
    expiresAfter,
    collapseKey,
    delayWhileIdle,
    restrictedPackageName,
    dryRun)
}

object GCMMessage {
  implicit val durationFormat = PrimitiveFormats.durationFormat
  implicit val json = Json.format[GCMMessage]

  type FCMMessage = GCMMessage
}
