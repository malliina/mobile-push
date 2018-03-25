package com.malliina.push.gcm

import play.api.libs.json.Json

import scala.concurrent.duration.Duration

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
  implicit val durationFormat = com.malliina.json.PrimitiveFormats.durationFormat
  implicit val json = Json.format[GCMMessage]
}
