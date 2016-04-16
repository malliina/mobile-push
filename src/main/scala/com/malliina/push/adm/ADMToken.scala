package com.malliina.push.adm

import com.malliina.push.{Token, TokenCompanion}

case class ADMToken(token: String) extends Token

object ADMToken extends TokenCompanion[ADMToken]
