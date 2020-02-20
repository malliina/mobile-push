package com.malliina.push.apns

import com.malliina.push.{SimpleCompanion, Token, TokenCompanion, ValidatingCompanion}
import com.malliina.values.StringEnumCompanion
import com.notnoop.apns.internal.Utilities
import play.api.libs.json.Json

import scala.util.Try

case class APNSTopic(topic: String) extends AnyVal

object APNSTopic extends SimpleCompanion[String, APNSTopic] {
  override def write(t: APNSTopic): String = t.topic
}

case class APNSHttpResult(token: APNSToken, id: Option[APNSIdentifier], error: Option[APNSError])

object APNSHttpResult {
  implicit val json = Json.format[APNSHttpResult]
}

abstract sealed class APNSPriority(val priority: Int)

object APNSPriority extends ValidatingCompanion[Int, APNSPriority] {
  override def build(input: Int): Option[APNSPriority] = input match {
    case APNSImmediately.priority => Option(APNSImmediately)
    case APNSConsiderate.priority => Option(APNSConsiderate)
    case _                        => None
  }

  override def write(t: APNSPriority): Int = t.priority
}

case object APNSImmediately extends APNSPriority(10)
case object APNSConsiderate extends APNSPriority(5)

abstract sealed class APNSPushType(val name: String)

object APNSPushType extends StringEnumCompanion[APNSPushType] {
  override def all: Seq[APNSPushType] = Seq(Alert, Background)
  override def write(t: APNSPushType) = t.name
}

case object Alert extends APNSPushType("alert")
case object Background extends APNSPushType("background")

case class APNSIdentifier(id: String) extends AnyVal {
  override def toString = id
}

object APNSIdentifier extends SimpleCompanion[String, APNSIdentifier] {
  override def write(t: APNSIdentifier): String = t.id
}

case class APNSMeta(
  apnsTopic: APNSTopic,
  apnsExpiration: Long,
  apnsPriority: APNSPriority,
  apnsPushType: APNSPushType,
  apnsId: Option[APNSIdentifier]
)

object APNSMeta {
  implicit val json = Json.format[APNSMeta]

  def withTopic(topic: APNSTopic) = APNSMeta(topic, 0, APNSImmediately, Alert, None)
}

case class APNSToken private (token: String) extends AnyVal with Token

object APNSToken extends TokenCompanion[APNSToken] {
  override def isValid(token: String): Boolean =
    Try(Utilities.decodeHex(token)).isSuccess
}
