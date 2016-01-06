package com.malliina.push.gcm

import com.malliina.push.{Token, TokenCompanion}

/**
  * @author mle
  */
case class GCMToken(token: String) extends Token

object GCMToken extends TokenCompanion[GCMToken]
