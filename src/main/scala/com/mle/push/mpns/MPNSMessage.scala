package com.mle.push.mpns

import scala.xml.Elem

/**
 * @author Michael
 */
trait MPNSMessage {
  def xml: Elem

  def headers: Map[String, String]
}

trait TileMessage extends MPNSMessage {
  override def headers: Map[String, String] = MPNSClient.tileHeaders
}

case class ToastMessage(text1: String,
                        text2: String,
                        deepLink: String,
                        silent: Boolean) extends MPNSMessage {
  override def xml: Elem = MPNSPayloads.toast(this)

  override def headers: Map[String, String] = MPNSClient.toastHeaders
}

case class TileData(backgroundImage: String,
                    count: Int,
                    title: String,
                    backBackgroundImage: String,
                    backTitle: String,
                    backContent: String) extends TileMessage {
  override def xml: Elem = MPNSPayloads.tile(this)
}

case class FlipData(smallBackgroundImage: String,
                    wideBackgroundImage: String,
                    wideBackBackgroundImage: String,
                    wideBackContent: String,
                    tile: TileData) extends TileMessage {
  override def xml: Elem = MPNSPayloads.flip(this)
}

case class IconicData(smallIconImage: String,
                      iconImage: String,
                      wideContent1: String,
                      wideContent2: String,
                      wideContent3: String,
                      count: Int,
                      title: String,
                      backgroundColor: String) extends TileMessage {
  override def xml: Elem = MPNSPayloads.iconic(this)
}

case class CycleTile(smallBackgroundImage: String,
                     cycleImage1: String,
                     cycleImage2: String,
                     cycleImage3: String,
                     cycleImage4: String,
                     cycleImage5: String,
                     cycleImage6: String,
                     cycleImage7: String,
                     cycleImage8: String,
                     cycleImage9: String,
                     count: Int,
                     title: String) extends TileMessage {
  override def xml: Elem = MPNSPayloads.cycle(this)
}
