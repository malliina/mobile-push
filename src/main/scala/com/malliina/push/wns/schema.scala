package com.malliina.push.wns

import java.net.URL

import scala.xml.{Attribute, Elem, NodeSeq, Text}

case class TileElement(visual: Visual[TileTemplate]) extends XmlNotification {

  override def notificationType: NotificationType = NotificationType.Tile

  override def xml: Elem =
    <tile>
      {visual.xml}
    </tile>
}

case class ToastElement(visual: Visual[ToastTemplate],
                        actions: Actions = Actions(),
                        launch: Option[String] = None,
                        activationType: Option[ActivationType] = None,
                        scenario: Option[Scenario] = None,
                        audio: Option[Audio] = None) extends XmlNotification {
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
  def text(text: String) = ToastElement(Visual.toastText(text))
}

case class Badge(value: BadgeValue = BadgeValue.None) extends XmlNotification {

  override def notificationType: NotificationType = NotificationType.Badge

  override def xml: Elem = <badge/>.withAttributes(
    "value" -> Option(value.name)
  )
}

/**
  *
  * @param payload base64-encoded
  */
case class Raw(payload: String) extends WNSNotification {
  override def notificationType: NotificationType = NotificationType.Raw
}

case class Commands(commands: Seq[Command]) extends Xmlable {
  override def xml: Elem = <commands>
    {commands.map(_.xml)}
  </commands>
}

case class Command(arguments: Option[String], id: Option[CommandId]) extends Xmlable {
  override def xml: Elem =
      <command/>.withAttributes(
      "arguments" -> arguments,
      "id" -> id
    )
}

case class Actions(inputs: Seq[Input] = Nil,
                   actions: Seq[ActionElement] = Nil) extends Xmlable {
  val isEmpty = actions.isEmpty

  override def xml: Elem =
    <actions>
      {inputs.map(_.xml)}
      {actions.map(_.xml)}
    </actions>
}

case class ActionElement(content: String,
                         arguments: String,
                         activationType: ActivationType,
                         imageUri: Option[String] = None,
                         hintInputId: Option[String] = None) extends Xmlable {
  override def xml: Elem = <action/>.withAttributes(
    "content" -> Option(content),
    "arguments" -> Option(arguments),
    "activationType" -> Option(activationType),
    "imageUri" -> imageUri,
    "hint-inputId" -> hintInputId
  )
}

case class Input(id: String,
                 inputType: InputType,
                 selection: Seq[Selection],
                 defaultInput: Option[String] = None,
                 title: Option[String] = None,
                 placeHolderContent: Option[String] = None) extends Xmlable {
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

case class Selection(id: String, content: String) extends Xmlable {
  override def xml: Elem = <selection/>.withAttributes(
    "id" -> Option(id),
    "content" -> Option(content)
  )
}

case class Visual[T <: Template](bindings: Seq[Binding[T]],
                                 lang: Option[String] = None,
                                 baseUri: Option[URL] = None,
                                 branding: Option[Branding] = None,
                                 addImageQuery: Option[Boolean] = None,
                                 contentId: Option[String] = None,
                                 displayName: Option[String] = None) extends Xmlable {
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

object Visual {
  def toastText(text: String) = Visual(Seq(Binding.toastText(text)))
}

case class Binding[T <: Template](template: T,
                                  texts: Seq[WnsText],
                                  images: Seq[Image] = Nil,
                                  groups: Seq[Group] = Nil,
                                  lang: Option[String] = None,
                                  baseUri: Option[URL] = None,
                                  branding: Option[Branding] = None,
                                  addImageQuery: Option[Boolean] = None,
                                  contentId: Option[String] = None,
                                  displayName: Option[String] = None,
                                  hintOverlay: Option[Int] = None) extends Xmlable {
  override def xml: Elem =
    <binding>
      {texts.map(_.xml)}
      {images.map(_.xml)}
      {groups.map(_.xml)}
    </binding>.withAttributes(
      "template" -> Option(template),
      "lang" -> lang,
      "baseUri" -> baseUri,
      "branding" -> branding,
      "addImageQuery" -> addImageQuery,
      "contentId" -> contentId,
      "displayName" -> displayName,
      "hint-overlay" -> hintOverlay
    )
}

object Binding {
  def toastText(text: String) =
    Binding[ToastTemplate](ToastTemplate.ToastGeneric, Seq(WnsText(text)))
}

case class Image(src: String,
                 placement: Option[Placement] = None,
                 alt: Option[String] = None,
                 addImageQuery: Option[Boolean] = None,
                 hintCrop: Option[HintCrop] = None,
                 hintRemoveMargin: Option[Boolean] = None,
                 hintAlign: Option[HintAlign] = None,
                 hintOverlay: Option[Int] = None) extends Xmlable {
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

case class WnsText(text: String,
                   lang: Option[String] = None,
                   hintStyle: Option[TextStyle] = None,
                   hintWrap: Option[Boolean] = None,
                   hintMaxLines: Option[Int] = None,
                   hintMinLines: Option[Int] = None,
                   hintAlign: Option[HintAlign] = None) extends Xmlable {
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

case class Audio(src: Option[String] = None,
                 silent: Boolean = false,
                 loop: Boolean = false) extends Xmlable {

  def xml: Elem = <audio/>.withAttributes(
    "src" -> src,
    "silent" -> Option(silent),
    "loop" -> Option(loop)
  )
}

object Audio {
  val Default = Audio()
  val Mute = Audio(silent = true)

  def once(source: String) = Audio(src = Option(source))
}

case class Group(subGroups: Seq[SubGroup]) extends Xmlable {
  override def xml: Elem =
    <group>
      {subGroups.map(_.xml)}
    </group>
}

case class SubGroup(hintWeight: Option[Int],
                    hintTextStacking: Option[TextStacking],
                    texts: Seq[WnsText],
                    images: Seq[Image]) extends Xmlable {
  override def xml: Elem =
    <subgroup>
      {texts.map(_.xml)}
      {images.map(_.xml)}
    </subgroup>.withAttributes(
      "hint-weight" -> hintWeight,
      "hint-textStacking" -> hintTextStacking
    )
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
