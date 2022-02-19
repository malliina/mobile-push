package com.malliina.push.json

import io.circe._

trait JsonEnum[T] {
  def all: Seq[T]
  def resolveName(item: T): String
  def withName(name: String): Option[T] =
    all.find(i => resolveName(i).toLowerCase == name.toLowerCase)
  def allNames = all.map(resolveName).mkString(", ")

  implicit val json: Codec[T] = Codec.from(
    Decoder.decodeString.emap(s =>
      withName(s).toRight(s"Unknown name: $s. Must be one of: $allNames.")
    ),
    Encoder.encodeString.contramap(t => resolveName(t))
  )
}

trait OpenEnum[T] {
  def all: Seq[T]
  def resolveName(item: T): String
  def withName(name: String): T =
    all.find(i => resolveName(i).toLowerCase == name.toLowerCase).getOrElse(default(name))
  def default(name: String): T
  def allNames = all.map(resolveName).mkString(", ")

  implicit val json: Codec[T] = Codec.from(
    Decoder.decodeString.map(s => withName(s)),
    Encoder.encodeString.contramap(t => resolveName(t))
  )
}
