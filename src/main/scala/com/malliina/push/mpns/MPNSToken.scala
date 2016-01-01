package com.malliina.push.mpns

import java.net.URL

import com.malliina.push.{Token, TokenCompanion}

import scala.util.Try

/**
  * @author mle
  */
case class MPNSToken private(token: String) extends Token

object MPNSToken extends TokenCompanion[MPNSToken] {
  override def isValid(token: String): Boolean = {
    Try(new URL(token)).filter(_.getPath.length > 0).isSuccess
  }
}
