package com.malliina.push.gcm

import com.malliina.push.{Token, TokenCompanion}

case class GCMToken(token: String) extends Token

object GCMToken extends TokenCompanion[GCMToken]
