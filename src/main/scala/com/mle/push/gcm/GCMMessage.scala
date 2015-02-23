package com.mle.push.gcm

import com.mle.json.JsonFormats
import play.api.libs.json.Json

import scala.concurrent.duration.Duration

/**
 * @author Michael
 */
case class GCMMessage(data: Map[String, String],
                      expiresAfter: Option[Duration] = None,
                      collapseKey: Option[String] = None,
                      delayWhileIdle: Option[Boolean] = None,
                      restrictedPackageName: Option[String] = None,
                      dryRun: Option[Boolean] = None) {
  def toLetter(ids: Seq[String]) = GCMLetter(
    ids,
    data,
    expiresAfter,
    collapseKey,
    delayWhileIdle,
    restrictedPackageName,
    dryRun)
}

object GCMMessage {
  implicit val durationFormat = JsonFormats.durationFormat

  implicit val json = Json.format[GCMMessage]
}