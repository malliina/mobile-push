package com.malliina.push.apns

import java.nio.file.{Path, Paths}
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

import com.malliina.push.{ConfHelper, PushUtils}
import com.malliina.json.PrimitiveFormats.durationCodec
import com.malliina.values.ErrorMessage

import scala.io.{BufferedSource, Source}

/** Apple Team ID.
  */
case class TeamId(team: String) extends AnyVal {
  override def toString: String = team
}

/** Apple Developer Key ID.
  */
case class KeyId(id: String) extends AnyVal {
  override def toString: String = id
}

/** @param privateKey
  *   downloadable from Apple's developer website
  */
case class APNSTokenConf(privateKey: PKCS8EncodedKeySpec, keyId: KeyId, teamId: TeamId)

object APNSTokenConf extends ConfHelper[APNSTokenConf] {
  val DefaultFile: Path = PushUtils.userHome.resolve("keys/apns/jwt.conf")

  def default: Either[ErrorMessage, APNSTokenConf] = fromFile(DefaultFile)

  def parse(read: String => Either[ErrorMessage, String]): Either[ErrorMessage, APNSTokenConf] = {
    for {
      file <- read("private_key")
      teamId <- read("team_id")
      keyId <- read("key_id")
    } yield APNSTokenConf(Source.fromFile(file), KeyId(keyId), TeamId(teamId))
  }

  def apply(
    privateKey: Path,
    keyId: KeyId,
    teamId: TeamId
  ): APNSTokenConf = APNSTokenConf(
    new PKCS8EncodedKeySpec(Base64.getDecoder.decode(readKey(Source.fromFile(privateKey.toFile)))),
    keyId,
    teamId
  )

  def apply(
    privateKey: BufferedSource,
    keyId: KeyId,
    teamId: TeamId
  ): APNSTokenConf = APNSTokenConf(
    new PKCS8EncodedKeySpec(Base64.getDecoder.decode(readKey(privateKey))),
    keyId,
    teamId
  )

  private[this] def readKey(src: BufferedSource): String = {
    try src.getLines().toList.drop(1).init.mkString
    finally src.close()
  }
}
