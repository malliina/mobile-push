package com.malliina.push

import com.malliina.values.{ErrorMessage, Readable, ValidatingCompanion}
import io.circe.{Decoder, Encoder}

abstract class SimpleCompanion[In: Decoder: Encoder: Ordering: Readable, T]
  extends ValidatingCompanion[In, T] {
  def apply(in: In): T

  def build(input: In): Either[ErrorMessage, T] =
    if (isValid(input)) Right(apply(input))
    else Left(ErrorMessage(s"Invalid input: '$input'."))

  def isValid(in: In): Boolean = true
}
