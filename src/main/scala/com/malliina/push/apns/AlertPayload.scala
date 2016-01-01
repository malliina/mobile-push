package com.malliina.push.apns

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
 * @author Michael
 */
case class AlertPayload(body: String,
                        title: Option[String] = None,
                        launchImage: Option[String] = None,
                        actionLocKey: Option[String] = None,
                        locKey: Option[String] = None,
                        locArgs: Option[Seq[String]] = None,
                        titleLocKey: Option[String] = None,
                        titleLocArgs: Option[Seq[String]] = None)

object AlertPayload {
  implicit val json: Format[AlertPayload] = (
    (JsPath \ "body").format[String] and
    (JsPath \ "title").formatNullable[String] and
    (JsPath \ "launch-image").formatNullable[String] and
    (JsPath \ "action-loc-key").formatNullable[String] and
    (JsPath \ "loc-key").formatNullable[String] and
    (JsPath \ "loc-args").formatNullable[Seq[String]] and
    (JsPath \ "title-loc-key").formatNullable[String] and
    (JsPath \ "title-loc-args").formatNullable[Seq[String]]
    )(AlertPayload.apply _, unlift(AlertPayload.unapply))
}
