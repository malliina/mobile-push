package com.malliina.push.wns

sealed abstract class Scenario(val name: String) extends Named

object Scenario {

  case object Default extends Scenario("default")

  case object Alarm extends Scenario("alarm")

  case object Reminder extends Scenario("reminder")

  case object IncomingCall extends Scenario("incomingCall")

}
