package com.malliina.push.wns

import com.malliina.push.NamedCompanion

sealed abstract class Scenario(val name: String) extends Named

object Scenario extends NamedCompanion[Scenario] {
  override def all: Seq[Scenario] = Seq(Default, Alarm, Reminder, IncomingCall)

  case object Default extends Scenario("default")
  case object Alarm extends Scenario("alarm")
  case object Reminder extends Scenario("reminder")
  case object IncomingCall extends Scenario("incomingCall")
}
