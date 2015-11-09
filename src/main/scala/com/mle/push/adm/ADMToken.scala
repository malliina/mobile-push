package com.mle.push.adm

import com.mle.push.{Token, TokenCompanion}

/**
  * @author mle
  */
case class ADMToken(token: String) extends Token

object ADMToken extends TokenCompanion[ADMToken] {
  override def isValid(token: String): Boolean = true
}
