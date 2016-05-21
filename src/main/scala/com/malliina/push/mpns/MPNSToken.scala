package com.malliina.push.mpns

import java.net.URL

import com.malliina.push.{Token, TokenCompanion}

import scala.util.Try

case class MPNSToken private(token: String) extends Token

object MPNSToken extends TokenCompanion[MPNSToken] {
  override def isValid(token: String): Boolean =
    toUrl(token).isSuccess

  def toUrl(in: String): Try[URL] =
    Try(new URL(in)).filter(_.getPath.length > 0)
}
