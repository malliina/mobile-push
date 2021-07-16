package com.malliina.push

import com.malliina.push.wns.Named
import com.malliina.values.{ErrorMessage, ValidatingCompanion}

abstract class NamedCompanion[T <: Named] extends ValidatingCompanion[String, T] {
  def all: Seq[T]
  override def write(t: T): String = t.name
  override def build(input: String): Either[ErrorMessage, T] =
    all.find(_.name == input).toRight(ErrorMessage(s"Invalid input: '$input'."))
}
