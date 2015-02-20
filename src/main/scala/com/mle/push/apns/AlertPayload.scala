package com.mle.push.apns

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}


/**
 * @author Michael
 */
case class AlertPayload(title: String, body: String, launchImage: Option[String])

object AlertPayload {
  implicit val json2: Writes[AlertPayload] = (
    (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String] and
      (JsPath \ "launch-image").writeNullable[String]
    )(unlift(AlertPayload.unapply))
}