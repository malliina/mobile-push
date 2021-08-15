package com.malliina.push.mpns

import scala.xml.{Elem, NodeSeq}

/** Do not automatically format this file.
  */
object MPNSPayloads {
  def toast(message: ToastMessage): Elem =
    toast(message.text1, message.text2, message.deepLink, message.silent)

  /** @param text1 title
    * @param text2 message
    * @param deepLink The page to go to in app. For example: /page1.xaml?value1=1234&amp;value2=9876
    * @return
    */
  def toast(text1: String, text2: String, deepLink: String, silent: Boolean): Elem = {
    val silenceElement = if (silent) <wp:Sound Silent="true"/> else NodeSeq.Empty
    // payloads must be on same line of xml, do not let formatting mess it up
    <wp:Notification xmlns:wp="WPNotification">
      <wp:Toast>
        <wp:Text1>{text1}</wp:Text1>
        <wp:Text2>{text2}</wp:Text2>
        <wp:Param>{deepLink}</wp:Param>
        {silenceElement}
      </wp:Toast>
    </wp:Notification>
  }

  def tile(tile: TileData): Elem = {
    <wp:Notification xmlns:wp="WPNotification">
      <wp:Tile>
        <wp:BackgroundImage>{tile.backgroundImage}</wp:BackgroundImage>
        <wp:Count>{tile.count}</wp:Count>
        <wp:Title>{tile.title}</wp:Title>
        <wp:BackBackgroundImage>{tile.backBackgroundImage}</wp:BackBackgroundImage>
        <wp:BackTitle>{tile.backTitle}</wp:BackTitle>
        <wp:BackContent>{tile.backContent}</wp:BackContent>
      </wp:Tile>
    </wp:Notification>
  }

  def flip(flip: FlipData): Elem = {
    <wp:Notification xmlns:wp="WPNotification" Version="2.0">
      <wp:Tile Template="FlipTile">
        <wp:SmallBackgroundImage>{flip.smallBackgroundImage}</wp:SmallBackgroundImage>
        <wp:WideBackgroundImage>{flip.wideBackgroundImage}</wp:WideBackgroundImage>
        <wp:WideBackBackgroundImage>{flip.wideBackBackgroundImage}</wp:WideBackBackgroundImage>
        <wp:WideBackContent>{flip.wideBackContent}</wp:WideBackContent>
        <wp:BackgroundImage>{flip.tile.backgroundImage}</wp:BackgroundImage>
        <wp:Count>{flip.tile.count}</wp:Count>
        <wp:Title>{flip.tile.title}</wp:Title>
        <wp:BackBackgroundImage>{flip.tile.backBackgroundImage}</wp:BackBackgroundImage>
        <wp:BackTitle>{flip.tile.backTitle}</wp:BackTitle>
        <wp:BackContent>{flip.tile.backContent}</wp:BackContent>
      </wp:Tile>
    </wp:Notification>
  }

  def iconic(iconic: IconicData): Elem = {
    <wp:Notification xmlns:wp="WPNotification" Version="2.0">
      <wp:Tile Template="IconicTile">
        <wp:SmallIconImage>{iconic.smallIconImage}</wp:SmallIconImage>
        <wp:IconImage>{iconic.iconImage}</wp:IconImage>
        <wp:WideContent1>{iconic.wideContent1}</wp:WideContent1>
        <wp:WideContent2>{iconic.wideContent2}</wp:WideContent2>
        <wp:WideContent3>{iconic.wideContent3}</wp:WideContent3>
        <wp:Count>{iconic.count}</wp:Count>
        <wp:Title>{iconic.title}</wp:Title>
        <wp:BackgroundColor>{iconic.backgroundColor}</wp:BackgroundColor>
      </wp:Tile>
    </wp:Notification>
  }

  def cycle(tile: CycleTile): Elem = {
    <wp:Notification xmlns:wp="WPNotification" Version="2.0">
      <wp:Tile Template="CycleTile">
        <wp:SmallBackgroundImage>{tile.smallBackgroundImage}</wp:SmallBackgroundImage>
        <wp:CycleImage1>{tile.cycleImage1}</wp:CycleImage1>
        <wp:CycleImage2>{tile.cycleImage2}</wp:CycleImage2>
        <wp:CycleImage3>{tile.cycleImage3}</wp:CycleImage3>
        <wp:CycleImage4>{tile.cycleImage4}</wp:CycleImage4>
        <wp:CycleImage5>{tile.cycleImage5}</wp:CycleImage5>
        <wp:CycleImage6>{tile.cycleImage6}</wp:CycleImage6>
        <wp:CycleImage7>{tile.cycleImage7}</wp:CycleImage7>
        <wp:CycleImage8>{tile.cycleImage8}</wp:CycleImage8>
        <wp:CycleImage9>{tile.cycleImage9}</wp:CycleImage9>
      </wp:Tile>
    </wp:Notification>
  }
}
