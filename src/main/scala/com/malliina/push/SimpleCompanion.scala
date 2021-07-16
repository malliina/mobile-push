package com.malliina.push

import io.circe.{Decoder, Encoder}
import com.malliina.values.{ErrorMessage, ValidatingCompanion}

abstract class SimpleCompanion[In: Decoder: Encoder: Ordering, T]
  extends ValidatingCompanion[In, T] {
  def apply(in: In): T

  def build(input: In): Either[ErrorMessage, T] =
    if (isValid(input)) Right(apply(input))
    else Left(ErrorMessage(s"Invalid input: '$input'."))

  def isValid(in: In): Boolean = true
}
