package com.malliina.push.gcm

import com.malliina.json.PrimitiveFormats
import com.malliina.push.{Token, TokenCompanion}
import io.circe._
import io.circe.generic.semiauto._

import scala.concurrent.duration.Duration

case class GCMToken(token: String) extends AnyVal with Token

object GCMToken extends TokenCompanion[GCMToken] {
  type FCMToken = GCMToken
}

case class GCMResponse(
  multicast_id: Long,
  success: Int,
  failure: Int,
  canonical_ids: Int,
  results: Seq[GCMResult]
)

object GCMResponse {
  implicit val json: Codec[GCMResponse] = deriveCodec[GCMResponse]
}

case class GCMNotification(
  title: Option[String],
  body: Option[String],
  subtitle: Option[String],
  sound: Option[String],
  badge: Option[String],
  icon: Option[String],
  click_action: Option[String],
  body_loc_key: Option[String],
  title_loc_key: Option[String],
  android_channel_id: Option[String],
  tag: Option[String],
  color: Option[String]
)

object GCMNotification {
  implicit val json: Codec[GCMNotification] = deriveCodec[GCMNotification]
}

case class GCMLetter(
  registration_ids: Seq[GCMToken],
  data: Map[String, String],
  notification: Option[GCMNotification] = None,
  time_to_live: Option[Duration] = None,
  collapse_key: Option[String] = None,
  delay_while_idle: Option[Boolean] = None,
  restricted_package_name: Option[String] = None,
  dry_run: Option[Boolean] = None
)

object GCMLetter {
  implicit val durationJson: Codec[Duration] = PrimitiveFormats.durationCodec
  implicit val json: Codec[GCMLetter] = deriveCodec[GCMLetter]
}

case class GCMMessage(
  data: Map[String, String],
  notification: Option[GCMNotification] = None,
  expiresAfter: Option[Duration] = None,
  collapseKey: Option[String] = None,
  delayWhileIdle: Option[Boolean] = None,
  restrictedPackageName: Option[String] = None,
  dryRun: Option[Boolean] = None
) {
  def toLetter(ids: Seq[GCMToken]) =
    GCMLetter(
      ids,
      data,
      notification,
      expiresAfter,
      collapseKey,
      delayWhileIdle,
      restrictedPackageName,
      dryRun
    )
}

object GCMMessage {
  implicit val durationFormat: Codec[Duration] = PrimitiveFormats.durationCodec
  implicit val json: Codec[GCMMessage] = deriveCodec[GCMMessage]

  type FCMMessage = GCMMessage
}
