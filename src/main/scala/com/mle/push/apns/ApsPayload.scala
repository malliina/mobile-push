package com.mle.push.apns

import play.api.libs.json.Json._
import play.api.libs.json.{JsObject, JsValue, Writes}

/**
 *
 * @param alert Some(Left(...)) for a simple alert text, Some(Right(...)) for more verbose alert details, None for background notifications
 * @param badge badge number
 * @param sound rock.mp3
 */
case class APSPayload(alert: Option[Either[String, AlertPayload]],
                      badge: Option[Int] = None,
                      sound: Option[String] = None) {
  private def objectify[T](key: String, opt: Option[T])(implicit w: Writes[T]) =
    opt.fold(obj())(v => obj(key -> toJson(v)))

  def json: JsObject = {
    val alertJson = alert.fold(obj("content-available" -> 1))(e => obj("alert" -> e.fold(s => toJson(s), a => toJson(a))))
    val soundValue = objectify("sound", sound)
    val badgeValue = objectify("badge", badge)
    alertJson ++ badgeValue ++ soundValue
  }
}

object APSPayload {
  implicit val json = new Writes[APSPayload] {
    override def writes(o: APSPayload): JsValue = o.json
  }
}
