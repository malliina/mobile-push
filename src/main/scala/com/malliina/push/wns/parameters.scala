package com.malliina.push.wns

sealed abstract class HintCrop(val name: String) extends Named

case object NoHintCrop extends HintCrop("none")

case object Circle extends HintCrop("circle")

sealed abstract class Template(val name: String) extends Named

case object ToastGeneric extends Template("ToastGeneric")

sealed abstract class Placement(val name: String) extends Named

case object Inline extends Placement("inline")

case object AppLogoOverride extends Placement("appLogoOverride")

sealed abstract class ImagePlacement(val name: String) extends Named

object ImagePlacement {

  case object Inline extends ImagePlacement("inline")

  case object Background extends ImagePlacement("background")

  case object Peek extends ImagePlacement("peek")

}

sealed abstract class Branding(val name: String) extends Named

case object NoBranding extends Branding("none")

case object Logo extends Branding("logo")

case object Name extends Branding("name")

case object NameAndLogo extends Branding("nameAndLogo")

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

object CommandId {

  case object Snooze extends CommandId("snooze")

  case object Dismiss extends CommandId("dismiss")

  case object Video extends CommandId("video")

  case object Voice extends CommandId("voice")

  case object Decline extends CommandId("decline")

}
