package com.malliina.push.wns

import java.util.regex.Pattern

import com.malliina.push.mpns.MPNSToken
import com.malliina.push.{Token, TokenCompanion}

case class WNSToken private(token: String) extends Token

object WNSToken extends TokenCompanion[WNSToken] {
  val wnsRegex = Pattern compile """https://[^/]+\.notify\.windows\.com/.*"""

  override def isValid(in: String): Boolean =
    MPNSToken.toUrl(in).isSuccess && matchesRegex(in)

  def matchesRegex(in: String) = wnsRegex.matcher(in).find()
}
