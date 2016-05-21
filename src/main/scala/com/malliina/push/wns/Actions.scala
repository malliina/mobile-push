package com.malliina.push.wns

import play.api.libs.json.Json

import scala.xml.Elem

case class Actions(inputs: Seq[Input] = Nil,
                   actions: Seq[ActionElement] = Nil) extends Xmlable {
  val isEmpty = actions.isEmpty

  override def xml: Elem =
    <actions>
      {inputs.map(_.xml)}
      {actions.map(_.xml)}
    </actions>
}

object Actions {
  implicit val json = Json.format[Actions]
}
