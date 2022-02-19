package com.malliina.push.wns

import io.circe._
import io.circe.generic.semiauto._

import scala.xml.Elem

case class Actions(inputs: Seq[Input] = Nil, actions: Seq[ActionElement] = Nil) extends Xmlable {
  val isEmpty = actions.isEmpty

  override def xml: Elem =
    <actions>
      {inputs.map(_.xml)}
      {actions.map(_.xml)}
    </actions>
}

object Actions {
  implicit val json: Codec[Actions] = deriveCodec[Actions]
}
