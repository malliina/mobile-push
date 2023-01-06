package com.malliina.push.apns

import io.circe._
import io.circe.syntax._

case class APNSMessage(aps: APSPayload, data: Map[String, Json] = Map())

object APNSMessage {
  val Aps = "aps"
  val decoder: Decoder[APNSMessage] = Decoder.decodeMap[String, Json].emap { map =>
    for {
      apsJson <- map.get(Aps).toRight(s"Missing $Aps")
      aps <- apsJson.as[APSPayload].left.map(_.toString)
    } yield APNSMessage(aps, map - Aps)
  }
  val encoder: Encoder[APNSMessage] = new Encoder[APNSMessage] {
    final def apply(a: APNSMessage): Json =
      Json.obj(Aps -> a.aps.asJson).deepMerge(a.data.asJson)
  }
  implicit val json: Codec[APNSMessage] = Codec.from(decoder, encoder)

  def simple(alert: String): APNSMessage = simple(alert, None)

  def badged(alert: String, badge: Int): APNSMessage = simple(alert, Option(badge))

  /** @return
    *   A message that updates the app badge in the background: No message is shown and no sound is
    *   played.
    */
  def background(badge: Int): APNSMessage = APNSMessage(APSPayload(None, Option(badge)))

  private def simple(alert: String, badge: Option[Int]): APNSMessage =
    APNSMessage(APSPayload.simple(alert, badge))
}
