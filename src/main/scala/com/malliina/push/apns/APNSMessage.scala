package com.malliina.push.apns

import play.api.libs.json.Json._
import play.api.libs.json._

case class APNSMessage(aps: APSPayload, data: Map[String, JsValue] = Map())

object APNSMessage {
  val Aps = "aps"
  val reader = Reads[APNSMessage] { json =>
    for {
      aps <- (json \ Aps).validate[APSPayload]
      data <- json.validate[Map[String, JsValue]].map(_ - Aps)
    } yield APNSMessage(aps, data)
  }
  val writer = Writes[APNSMessage](o => obj(Aps -> toJson(o.aps)) ++ toJson(o.data).as[JsObject])
  implicit val json = Format(reader, writer)

  def simple(alert: String): APNSMessage = simple(alert, None)

  def badged(alert: String, badge: Int): APNSMessage = simple(alert, Option(badge))

  /**
    * @return A message that updates the app badge in the background: No message is shown and no sound is played.
    */
  def background(badge: Int): APNSMessage = APNSMessage(APSPayload(None, Option(badge)))

  private def simple(alert: String, badge: Option[Int]): APNSMessage =
    APNSMessage(APSPayload.simple(alert, badge))
}
