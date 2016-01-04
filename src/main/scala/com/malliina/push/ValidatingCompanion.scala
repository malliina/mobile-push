package com.malliina.push

import play.api.libs.json._

/**
  * @author mle
  */
abstract class ValidatingCompanion[In, T](implicit r: Format[In]) {
  private val reader = Reads[T](json => {
    json.validate[In].flatMap(s => build(s)
      .map[JsResult[T]](t => JsSuccess(t))
      .getOrElse(JsError(s"Invalid input: $s")))
  })
  private val writer = Writes[T](t => Json.toJson(write(t)))
  implicit val json = Format[T](reader, writer)

  def build(input: In): Option[T]

  def write(t: T): In
}
