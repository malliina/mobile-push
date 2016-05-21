package com.malliina.push.wns

import com.malliina.push.NamedCompanion

sealed abstract class ActivationType(val name: String) extends Named

object ActivationType extends NamedCompanion[ActivationType] {

  override val all: Seq[ActivationType] =
    Seq(Foreground, Background, Protocol, System)

  case object Foreground extends ActivationType("foreground")

  case object Background extends ActivationType("background")

  case object Protocol extends ActivationType("protocol")

  case object System extends ActivationType("system")

  val Default = Foreground
}
