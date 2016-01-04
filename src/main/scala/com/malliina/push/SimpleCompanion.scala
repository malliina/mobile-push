package com.malliina.push

import play.api.libs.json._

/**
  * @author mle
  */
abstract class SimpleCompanion[In, T](implicit r: Format[In])
  extends ValidatingCompanion[In, T] {
  def apply(in: In): T

  def build(input: In): Option[T] =
    if (isValid(input)) Option(apply(input))
    else None

  def isValid(in: In): Boolean = true
}
