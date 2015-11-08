package com.mle.push.gcm

import com.mle.push.{TokenCompanion, Token}

/**
  * @author mle
  */
case class GCMToken(token: String) extends Token

object GCMToken extends TokenCompanion[GCMToken] {
  override def isValid(token: String): Boolean = true
}
