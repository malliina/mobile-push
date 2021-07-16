package com.malliina.push.wns

import io.circe._
import io.circe.generic.semiauto._

import scala.xml.{Elem, NodeSeq}

case class ToastElement(
  visual: ToastVisual,
  actions: Actions = Actions(),
  launch: Option[String] = None,
  activationType: Option[ActivationType] = None,
  scenario: Option[Scenario] = None,
  audio: Option[Audio] = None
) extends XmlNotification {
  val actionsXml = if (actions.isEmpty) NodeSeq.Empty else actions.xml
  val audioXml = audio.map(_.xml) getOrElse NodeSeq.Empty

  override def notificationType: NotificationType = NotificationType.Toast

  override def xml: Elem =
    <toast>
      {visual.xml}
      {actionsXml}
      {audioXml}
    </toast>.withAttributes(
      "launch" -> launch,
      "activationType" -> activationType,
      "scenario" -> scenario
    )
}

object ToastElement {
  implicit val json: Codec[ToastElement] = deriveCodec[ToastElement]

  def text(text: String) = ToastElement(ToastVisual.text(text))
}
