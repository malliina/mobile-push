package com.malliina.push

/** Base companion object for `Token`s.
  */
trait TokenCompanion[T <: Token] extends SimpleCompanion[String, T] {
  override def write(t: T): String = t.token
}
