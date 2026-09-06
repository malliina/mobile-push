package com.malliina.push.adm

import com.malliina.push.{Token, TokenCompanion}
import com.malliina.values.ErrorMessage

case class ADMToken(token: String) extends AnyVal with Token

object ADMToken extends TokenCompanion[ADMToken]:
  override def build(input: String): Either[ErrorMessage, ADMToken] =
    if input.isBlank then Left(ErrorMessage("Token cannot be blank."))
    else Right(apply(input))
