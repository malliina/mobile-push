package com.malliina.push.wns

sealed abstract class NotificationType(val name: String)

object NotificationType {

  case object Badge extends NotificationType("wns/badge")

  case object Tile extends NotificationType("wns/tile")

  case object Toast extends NotificationType("wns/toast")

  case object Raw extends NotificationType("wns/raw")

}
