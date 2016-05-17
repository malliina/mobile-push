package com.malliina.push.wns

sealed abstract class ActivationType(val name: String) extends Named

object ActivationType {

  case object Foreground extends ActivationType("foreground")

  case object Background extends ActivationType("background")

  case object Protocol extends ActivationType("protocol")

  case object System extends ActivationType("system")

  val Default = Foreground
}
