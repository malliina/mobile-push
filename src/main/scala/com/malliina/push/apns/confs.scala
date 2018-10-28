package com.malliina.push.apns

import java.nio.file.{Path, Paths}

import com.malliina.push.ConfHelper
import com.malliina.values.ErrorMessage

/** Apple Team ID.
  */
case class TeamId(team: String)

/** Apple Developer Key ID.
  */
case class KeyId(id: String)

/**
  * @param privateKey downloadable from Apple's developer website
  */
case class APNSTokenConf(privateKey: Path, keyId: KeyId, teamId: TeamId)

object APNSTokenConf extends ConfHelper[APNSTokenConf] {
  val DefaultFile = (Paths get sys.props("user.home")).resolve("keys/apns/jwt.conf")

  def default = fromFile(DefaultFile)

  def parse(read: String => Either[ErrorMessage, String]): Either[ErrorMessage, APNSTokenConf] = {
    for {
      file <- read("private_key")
      teamId <- read("team_id")
      keyId <- read("key_id")
    } yield APNSTokenConf(Paths get file, KeyId(keyId), TeamId(teamId))
  }
}
