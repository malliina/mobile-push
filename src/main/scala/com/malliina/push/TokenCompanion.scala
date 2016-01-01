package com.malliina.push

import play.api.libs.json._

/** Base companion object for `Token`s.
  *
  * @author mle
  */
trait TokenCompanion[T <: Token] {
  private val reader = Reads[T](json => {
    json.validate[String].flatMap(s => build(s)
      .map[JsResult[T]](t => JsSuccess(t))
      .getOrElse(JsError(s"Invalid format of token: $s")))
  })
  private val writer = Writes[T](t => Json.toJson(t.token))
  implicit val json = Format[T](reader, writer)

  def apply(token: String): T

  def build(token: String): Option[T] =
    if (isValid(token)) Option(apply(token))
    else None

  def isValid(token: String): Boolean
}
