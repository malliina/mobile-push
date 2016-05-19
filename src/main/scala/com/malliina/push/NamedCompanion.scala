package com.malliina.push

import com.malliina.push.wns.Named

abstract class NamedCompanion[T <: Named]
  extends ValidatingCompanion[String, T] {
  def all: Seq[T]

  override def write(t: T): String = t.name

  override def build(input: String): Option[T] = all.find(_.name == input)
}
