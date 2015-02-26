package com.mle.push.apns

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}


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
  implicit val json: Writes[AlertPayload] = (
    (JsPath \ "body").write[String] and
      (JsPath \ "title").writeNullable[String] and
      (JsPath \ "launch-image").writeNullable[String] and
      (JsPath \ "action-loc-key").writeNullable[String] and
      (JsPath \ "loc-key").writeNullable[String] and
      (JsPath \ "loc-args").writeNullable[Seq[String]] and
      (JsPath \ "title-loc-key").writeNullable[String] and
      (JsPath \ "title-loc-args").writeNullable[Seq[String]]
    )(unlift(AlertPayload.unapply))
}
