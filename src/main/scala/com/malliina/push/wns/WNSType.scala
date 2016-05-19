package com.malliina.push.wns

sealed abstract class WNSType(val name: String)

object WNSType {

  case object Badge extends WNSType("wns/badge")

  case object Tile extends WNSType("wns/tile")

  case object Toast extends WNSType("wns/toast")

  case object Raw extends WNSType("wns/raw")

}
