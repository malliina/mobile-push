package com.malliina.push.apns

import com.malliina.push.{SimpleCompanion, Token, TokenCompanion}
import com.malliina.values.{ErrorMessage, StringEnumCompanion, ValidatingCompanion}
import io.circe._
import io.circe.generic.semiauto._

import scala.util.Try

case class APNSTopic(topic: String) extends AnyVal {
  override def toString: String = topic
}

object APNSTopic extends SimpleCompanion[String, APNSTopic] {
  override def write(t: APNSTopic): String = t.topic

  def liveActivity(bundleId: String): APNSTopic = apply(s"$bundleId.push-type.liveactivity")
}

case class APNSHttpResult(token: APNSToken, id: Option[APNSIdentifier], error: Option[APNSError])

object APNSHttpResult {
  implicit val json: Codec[APNSHttpResult] = deriveCodec[APNSHttpResult]
}

abstract sealed class APNSPriority(val priority: Int)

object APNSPriority extends ValidatingCompanion[Int, APNSPriority] {
  override def build(input: Int): Either[ErrorMessage, APNSPriority] = input match {
    case APNSImmediately.priority => Right(APNSImmediately)
    case APNSConsiderate.priority => Right(APNSConsiderate)
    case _                        => Left(ErrorMessage(s"Invalid input: '$input'."))
  }

  override def write(t: APNSPriority): Int = t.priority
}

case object APNSImmediately extends APNSPriority(10)
case object APNSConsiderate extends APNSPriority(5)

abstract sealed class APNSPushType(val name: String) {
  override def toString: String = name
}

object APNSPushType extends StringEnumCompanion[APNSPushType] {
  override def all: Seq[APNSPushType] = Seq(Alert, Background)
  override def write(t: APNSPushType): String = t.name
}

case object Alert extends APNSPushType("alert")
case object Background extends APNSPushType("background")
case object LiveActivity extends APNSPushType("liveactivity")

case class APNSIdentifier(id: String) extends AnyVal {
  override def toString: String = id
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
  implicit val json: Codec[APNSMeta] = deriveCodec[APNSMeta]

  def withTopic(
    topic: APNSTopic,
    pushType: APNSPushType = Alert,
    priority: APNSPriority = APNSImmediately
  ): APNSMeta =
    APNSMeta(topic, 0, priority, pushType, None)

  def liveActivity(topic: APNSTopic, priority: APNSPriority = APNSImmediately): APNSMeta =
    APNSMeta(topic, 0, priority, LiveActivity, None)
}

case class APNSToken(token: String) extends AnyVal with Token

object APNSToken extends TokenCompanion[APNSToken] {
  override def isValid(token: String): Boolean =
    Try(decodeHex(token)).isSuccess

  private def decodeHex(str: String): Array[Byte] = {
    str.sliding(2, 2).toArray.map(h => Integer.parseInt(h, 16).toByte)
  }
}

case class InactiveDevice(deviceHexID: String, asOf: Long)
