package com.malliina.push.apns

import java.nio.file.{Path, Paths}

import com.malliina.push.{ConfHelper, PushUtils, durationFormat}
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

/**
  * @param privateKey downloadable from Apple's developer website
  */
case class APNSTokenConf(privateKey: BufferedSource, keyId: KeyId, teamId: TeamId)

object APNSTokenConf extends ConfHelper[APNSTokenConf] {
  val DefaultFile: Path = PushUtils.userHome.resolve("keys/apns/jwt.conf")

  def default = fromFile(DefaultFile)

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
  ): APNSTokenConf = new APNSTokenConf(
    Source.fromFile(privateKey.toFile),
    keyId,
    teamId
  )

  def apply(
    privateKey: BufferedSource,
    keyId: KeyId,
    teamId: TeamId
  ): APNSTokenConf = new APNSTokenConf(
    privateKey,
    keyId,
    teamId
  )
}
