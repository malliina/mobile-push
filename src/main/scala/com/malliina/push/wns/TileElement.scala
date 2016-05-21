package com.malliina.push.wns

import play.api.libs.json.Json

import scala.xml.Elem

case class TileElement(visual: TileVisual) extends XmlNotification {

  override def notificationType: NotificationType = NotificationType.Tile

  override def xml: Elem =
    <tile>
      {visual.xml}
    </tile>
}

object TileElement {
  implicit val json = Json.format[TileElement]
}
