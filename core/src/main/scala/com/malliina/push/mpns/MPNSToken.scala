package com.malliina.push.mpns

import com.malliina.push.{Token, TokenCompanion}
import com.malliina.values.ErrorMessage

import java.net.URL
import scala.util.Try

case class MPNSToken(token: String) extends AnyVal with Token

object MPNSToken extends TokenCompanion[MPNSToken]:
  override def build(input: String): Either[ErrorMessage, MPNSToken] =
    if isValid(input) then Right(apply(input))
    else Left(defaultError(input))

  def isValid(token: String): Boolean =
    toUrl(token).isSuccess

  def toUrl(in: String): Try[URL] =
    Try(new URL(in)).filter(_.getPath.length > 0)
