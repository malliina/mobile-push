package com.malliina.push.mpns

import com.malliina.push.{Token, TokenCompanion}

import java.net.URL
import scala.util.Try

case class MPNSToken(token: String) extends AnyVal with Token

object MPNSToken extends TokenCompanion[MPNSToken] {
  override def isValid(token: String): Boolean =
    toUrl(token).isSuccess

  def toUrl(in: String): Try[URL] =
    Try(new URL(in)).filter(_.getPath.length > 0)
}
