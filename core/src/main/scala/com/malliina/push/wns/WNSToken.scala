package com.malliina.push.wns

import java.util.regex.Pattern
import com.malliina.push.mpns.MPNSToken
import com.malliina.push.{Token, TokenCompanion}
import com.malliina.values.ErrorMessage

case class WNSToken(token: String) extends AnyVal with Token

object WNSToken extends TokenCompanion[WNSToken]:
  val wnsRegex = Pattern.compile("""https://[^/]+\.notify\.windows\.com/.*""")

  override def build(input: String): Either[ErrorMessage, WNSToken] =
    if isValid(input) then Right(apply(input))
    else Left(defaultError(input))

  def isValid(in: String): Boolean =
    MPNSToken.toUrl(in).isSuccess && matchesRegex(in)

  def matchesRegex(in: String) = wnsRegex.matcher(in).find()
