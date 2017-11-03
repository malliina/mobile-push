package com.malliina.push.apns

import java.nio.file.{Path, Paths}

import com.malliina.file.{FileUtilities, StorageFile}
import com.malliina.util.BaseConfigReader

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

object APNSConfLoader {
  val DefaultFile = FileUtilities.userHome / "keys" / "apns" / "jwt.conf"

  def default = fromFile(DefaultFile)

  def fromFile(file: Path): APNSConfLoader = new APNSConfLoader(file)
}

class APNSConfLoader(file: Path) extends BaseConfigReader[APNSTokenConf] {
  override def filePath: Option[Path] = Option(file)

  override def loadOpt: Option[APNSTokenConf] = fromUserHomeOpt

  override def fromMapOpt(map: Map[String, String]): Option[APNSTokenConf] = for {
    file <- map get "private_key"
    teamId <- map get "team_id"
    keyId <- map get "key_id"
  } yield APNSTokenConf(Paths get file, KeyId(keyId), TeamId(teamId))
}
