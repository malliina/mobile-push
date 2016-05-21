package com.malliina.push.wns

import com.malliina.push.NamedCompanion

sealed abstract class HintCrop(val name: String) extends Named

object HintCrop {

  case object NoHintCrop extends HintCrop("none")

  case object Circle extends HintCrop("circle")

}

sealed abstract class ToastTemplate(val name: String) extends Template

object ToastTemplate {

  case object ToastGeneric extends ToastTemplate("ToastGeneric")

}

sealed abstract class Placement(val name: String) extends Named

object Placement {

  case object Inline extends Placement("inline")

  case object Background extends Placement("background")

  case object AppLogoOverride extends Placement("appLogoOverride")

  case object Peek extends Placement("peek")

}

sealed abstract class Branding(val name: String) extends Named

object Branding {

  case object NoBranding extends Branding("none")

  case object Logo extends Branding("logo")

  case object Name extends Branding("name")

  case object NameAndLogo extends Branding("nameAndLogo")

}

sealed abstract class HintAlign(val name: String) extends Named

object HintAlign {

  case object Left extends HintAlign("left")

  case object Center extends HintAlign("center")

  case object Right extends HintAlign("right")

  case object Stretch extends HintAlign("stretch")

}

sealed abstract class TextStacking(val name: String) extends Named

object TextStacking {

  case object Top extends TextStacking("top")

  case object Center extends TextStacking("center")

  case object Bottom extends TextStacking("bottom")

}

sealed abstract class InputType(val name: String) extends Named

object InputType {

  case object Text extends InputType("text")

  case object Selection extends InputType("selection")

}

sealed abstract class CommandId(val name: String) extends Named

object CommandId extends NamedCompanion[CommandId] {

  override val all: Seq[CommandId] =
    Seq(Snooze, Dismiss, Video, Voice, Decline)

  case object Snooze extends CommandId("snooze")

  case object Dismiss extends CommandId("dismiss")

  case object Video extends CommandId("video")

  case object Voice extends CommandId("voice")

  case object Decline extends CommandId("decline")

}

sealed abstract class BadgeValue(val name: String) extends Named

object BadgeValue {

  case class Number(num: Int) extends BadgeValue(num.toString)

  case object None extends BadgeValue("none")

  case object Activity extends BadgeValue("activity")

  case object Alert extends BadgeValue("alert")

  case object Alarm extends BadgeValue("alarm")

  case object Available extends BadgeValue("available")

  case object Away extends BadgeValue("away")

  case object Busy extends BadgeValue("busy")

  case object NewMessage extends BadgeValue("newMessage")

  case object Paused extends BadgeValue("paused")

  case object Playing extends BadgeValue("playing")

  case object Unavailable extends BadgeValue("unavailable")

  case object Error extends BadgeValue("error")

  case object Attention extends BadgeValue("attention")

}

sealed abstract class TileTemplate(val name: String) extends Template

object TileTemplate {

  case object TileSmall extends TileTemplate("TileSmall")

  case object TileMedium extends TileTemplate("TileMedium")

  case object TileWide extends TileTemplate("TileWide")

  case object TileLarge extends TileTemplate("TileLarge")

}

sealed trait Template extends Named

sealed abstract class TextStyle(val name: String) extends Named

object TextStyle {

  case object Caption extends TextStyle("caption")

  case object CaptionSubtle extends TextStyle("captionSubtle")

  case object Body extends TextStyle("body")

  case object BodySubtle extends TextStyle("bodySubtle")

  case object Base extends TextStyle("base")

  case object BaseSubtle extends TextStyle("baseSubtle")

  case object Subtitle extends TextStyle("subtitle")

  case object SubtitleSubtle extends TextStyle("subtitleSubtle")

  case object Title extends TextStyle("title")

  case object TitleSubtle extends TextStyle("titleSubtle")

  case object TitleNumeral extends TextStyle("titleNumeral")

  case object Subheader extends TextStyle("subheader")

  case object SubheaderSubtle extends TextStyle("subheaderSubtle")

  case object SubheaderNumeral extends TextStyle("subheaderNumeral")

  case object Header extends TextStyle("header")

  case object HeaderSubtle extends TextStyle("headerSubtle")

  case object HeaderNumber extends TextStyle("headerNumber")

}
