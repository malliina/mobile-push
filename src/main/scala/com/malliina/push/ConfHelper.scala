package com.malliina.push

import com.malliina.values.ErrorMessage

import java.nio.file.Path

trait ConfHelper[T] {
  def load(file: Path): T = fromFile(file).fold(msg => throw new Exception(msg.message), identity)

  def fromFile(file: Path): Either[ErrorMessage, T] = fromConf(PushUtils.props(file))

  def fromConf(map: Map[String, String]): Either[ErrorMessage, T] = {
    def read(key: String) = map.get(key).toRight(ErrorMessage(s"Key missing: '$key'."))

    parse(read)
  }

  def parse(readKey: String => Either[ErrorMessage, String]): Either[ErrorMessage, T]
}
