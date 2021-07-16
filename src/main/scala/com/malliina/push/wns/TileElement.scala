package com.malliina.push.wns

import io.circe._
import io.circe.generic.semiauto._

import scala.xml.Elem

case class TileElement(visual: TileVisual) extends XmlNotification {

  override def notificationType: NotificationType = NotificationType.Tile

  override def xml: Elem =
    <tile>
      {visual.xml}
    </tile>
}

object TileElement {
  implicit val json: Codec[TileElement] = deriveCodec[TileElement]
}
