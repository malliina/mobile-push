package com.malliina.push.wns

import java.net.URL

import play.api.libs.json._

import scala.xml.{Attribute, Elem, Text}

case class Badge(value: BadgeValue = BadgeValue.None) extends XmlNotification {

  override def notificationType: NotificationType = NotificationType.Badge

  override def xml: Elem = <badge/>.withAttributes(
    "value" -> Option(value.name)
  )
}

object Badge {
  implicit val json = Json.format[Badge]
}

/**
  *
  * @param payload base64-encoded
  */
case class Raw(payload: String) extends WNSNotification {
  override def notificationType: NotificationType = NotificationType.Raw
}

object Raw {
  implicit val json = Json.format[Raw]
}

case class Commands(commands: Seq[Command]) extends Xmlable {
  override def xml: Elem = <commands>
    {commands.map(_.xml)}
  </commands>
}

object Commands {
  implicit val cJson = Command.json
  implicit val json = Json.format[Commands]
}

case class Command(arguments: Option[String], id: Option[CommandId]) extends Xmlable {
  override def xml: Elem =
    <command/>.withAttributes(
      "arguments" -> arguments,
      "id" -> id
    )
}

object Command {
  implicit val json = Json.format[Command]
}

case class ActionElement(
  content: String,
  arguments: String,
  activationType: ActivationType,
  imageUri: Option[String] = None,
  hintInputId: Option[String] = None
) extends Xmlable {
  override def xml: Elem = <action/>.withAttributes(
    "content" -> Option(content),
    "arguments" -> Option(arguments),
    "activationType" -> Option(activationType),
    "imageUri" -> imageUri,
    "hint-inputId" -> hintInputId
  )
}

object ActionElement {
  implicit val json = Json.format[ActionElement]
}

case class Selection(id: String, content: String) extends Xmlable {
  override def xml: Elem = <selection/>.withAttributes(
    "id" -> Option(id),
    "content" -> Option(content)
  )
}

object Selection {
  implicit val json = Json.format[Selection]
}

case class Input(
  id: String,
  inputType: InputType,
  selection: Seq[Selection],
  defaultInput: Option[String] = None,
  title: Option[String] = None,
  placeHolderContent: Option[String] = None
) extends Xmlable {
  override def xml: Elem =
    <input>
      {selection.map(_.xml)}
    </input>.withAttributes(
      "id" -> Option(id),
      "type" -> Option(inputType),
      "title" -> title,
      "placeHolderContent" -> placeHolderContent,
      "defaultInput" -> defaultInput
    )
}

object Input {
  implicit val json = Json.format[Input]
}

case class ToastVisual(
  bindings: Seq[ToastBinding],
  lang: Option[String] = None,
  baseUri: Option[URL] = None,
  branding: Option[Branding] = None,
  addImageQuery: Option[Boolean] = None,
  contentId: Option[String] = None,
  displayName: Option[String] = None
) extends Visual[ToastTemplate]

object ToastVisual {
  implicit val url = Binding.urlFormat
  implicit val json = Json.format[ToastVisual]

  def text(text: String) = ToastVisual(Seq(ToastBinding.text(text)))
}

case class TileVisual(
  bindings: Seq[TileBinding],
  lang: Option[String] = None,
  baseUri: Option[URL] = None,
  branding: Option[Branding] = None,
  addImageQuery: Option[Boolean] = None,
  contentId: Option[String] = None,
  displayName: Option[String] = None
) extends Visual[TileTemplate]

object TileVisual {
  implicit val url = Binding.urlFormat
  implicit val json = Json.format[TileVisual]
}

trait Visual[T <: Template] extends Xmlable {
  def bindings: Seq[Binding[T]]

  def lang: Option[String]

  def baseUri: Option[URL]

  def branding: Option[Branding]

  def addImageQuery: Option[Boolean]

  def contentId: Option[String]

  def displayName: Option[String]

  override def xml: Elem =
    <visual>
      {bindings.map(_.xml)}
    </visual>.withAttributes(
      "lane" -> lang,
      "baseUri" -> baseUri,
      "branding" -> branding,
      "addImageQuery" -> addImageQuery,
      "contentId" -> contentId,
      "displayName" -> displayName
    )
}

case class Image(
  src: String,
  placement: Option[Placement] = None,
  alt: Option[String] = None,
  addImageQuery: Option[Boolean] = None,
  hintCrop: Option[HintCrop] = None,
  hintRemoveMargin: Option[Boolean] = None,
  hintAlign: Option[HintAlign] = None,
  hintOverlay: Option[Int] = None
) extends Xmlable {
  override def xml: Elem = <image/>.withAttributes(
    "src" -> Option(src),
    "placement" -> placement,
    "alt" -> alt,
    "addImageQuery" -> addImageQuery,
    "hint-crop" -> hintCrop,
    "hint-removeMargin" -> hintRemoveMargin,
    "hint-align" -> hintAlign,
    "hint-overlay" -> hintOverlay
  )
}

object Image {
  implicit val json = Json.format[Image]
}

case class WnsText(
  text: String,
  lang: Option[String] = None,
  hintStyle: Option[TextStyle] = None,
  hintWrap: Option[Boolean] = None,
  hintMaxLines: Option[Int] = None,
  hintMinLines: Option[Int] = None,
  hintAlign: Option[HintAlign] = None
) extends Xmlable {
  def xml: Elem =
    <text>
      {text}
    </text>.withAttributes(
      "lang" -> lang,
      "hint-style" -> hintStyle,
      "hint-wrap" -> hintWrap,
      "hint-maxLines" -> hintMaxLines,
      "hint-minLines" -> hintMinLines,
      "hint-align" -> hintAlign
    )
}

object WnsText {
  implicit val json = Json.format[WnsText]
}

case class Audio(src: Option[String] = None, silent: Boolean = false, loop: Boolean = false)
  extends Xmlable {

  def xml: Elem = <audio/>.withAttributes(
    "src" -> src,
    "silent" -> Option(silent),
    "loop" -> Option(loop)
  )
}

object Audio {
  implicit val json = Json.format[Audio]
  val Default = Audio()
  val Mute = Audio(silent = true)

  def once(source: String) = Audio(src = Option(source))
}

case class SubGroup(
  hintWeight: Option[Int],
  hintTextStacking: Option[TextStacking],
  texts: Seq[WnsText],
  images: Seq[Image]
) extends Xmlable {
  override def xml: Elem =
    <subgroup>
      {texts.map(_.xml)}
      {images.map(_.xml)}
    </subgroup>.withAttributes(
      "hint-weight" -> hintWeight,
      "hint-textStacking" -> hintTextStacking
    )
}

object SubGroup {
  implicit val json = Json.format[SubGroup]
}

case class Group(subGroups: Seq[SubGroup]) extends Xmlable {
  override def xml: Elem =
    <group>
      {subGroups.map(_.xml)}
    </group>
}

object Group {
  implicit val json = Json.format[Group]
}

trait Xmlable extends XmlOps {
  def xml: Elem
}

trait XmlOps {

  implicit class ElemOps(e: Elem) {
    def withAttributes(kvs: (String, Option[Any])*) =
      addMap(e, filterAndStringify(kvs.toMap))
  }

  def filterAndStringify[K, V](m: Map[K, Option[V]]): Map[K, String] =
    filterOption(m) map {
      case (key, value) => (key, value.toString)
    }

  def filterOption[K, V](m: Map[K, Option[V]]): Map[K, V] =
    m filter {
      case (_, value) => value.isDefined
    } map {
      case (key, value) => (key, value.get)
    }

  def addMap(xml: Elem, attributes: Map[String, String]) =
    attributes.foldLeft(xml)((elem, pair) => add(elem, pair._1, pair._2))

  def add(xml: Elem, key: String, value: String): Elem =
    xml % Attribute(None, key, Text(value), scala.xml.Null)
}
