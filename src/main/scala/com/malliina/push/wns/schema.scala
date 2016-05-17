package com.malliina.push.wns

import java.net.URL

import scala.xml.{Attribute, Elem, NodeSeq, Text}

case class TileElement(visual: Visual)

case class ToastElement(launch: Option[String],
                        activationType: ActivationType,
                        scenario: Scenario,
                        actions: Actions,
                        visual: Visual,
                        audio: Option[Audio]) extends Xmlable {
  val actionsXml = if(actions.isEmpty) NodeSeq.Empty else actions.xml

  override def xml: Elem =
    <toast>
      {actionsXml}
    </toast>.withAttributes(
      "launch" -> launch,
      "activationType" -> Option(activationType),
      "scenario" -> Option(scenario)
    )
}

case class Commands(commands: Seq[Command]) extends Xmlable {
  override def xml: Elem = <commands>{commands.map(_.xml)}</commands>
}
case class Command(arguments: Option[String], id: Option[CommandId]) extends Xmlable {
  override def xml: Elem =
    <command/>.withAttributes(
      "arguments" -> arguments,
      "id" -> id
    )
}

case class Actions(actions: Seq[ActionElement]) extends Xmlable {
  val isEmpty = actions.isEmpty

  override def xml: Elem =
    <actions>
      {actions.map(_.xml)}
    </actions>
}
case class ActionElement(content: String,
                         arguments: String,
                         activationType: ActivationType,
                         imageUri: Option[String],
                         hintInputId: String) extends Xmlable {
  override def xml: Elem = <action/>.withAttributes(
    "content" -> Option(content),
    "arguments" -> Option(arguments),
    "activationType" -> Option(activationType),
    "imageUri" -> imageUri,
    "hint-inputId" -> Option(hintInputId)
  )
}
case class Input(id: String,
                 inputType: InputType,
                 title: Option[String],
                 placeHolderContent: Option[String],
                 defaultInput: Option[String],
                 selection: Seq[Selection]) extends Xmlable {
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

case class Selection(id: String, selection: String) extends Xmlable {
  override def xml: Elem = <selection/>.withAttributes(
    "id" -> Option(id),
    "selection" -> Option(selection)
  )
}

case class Visual(lang: Option[String],
                  baseUri: Option[URL],
                  branding: Option[Branding],
                  addImageQuery: Boolean,
                  contentId: Option[String],
                  displayName: Option[String],
                  bindings: Seq[Binding]) extends Xmlable {
  override def xml: Elem =
    <visual>
      {bindings.map(_.xml)}
    </visual>.withAttributes(
      "lane" -> lang,
      "baseUri" -> baseUri,
      "branding" -> branding,
      "addImageQuery" -> Option(addImageQuery),
      "contentId" -> contentId,
      "displayName" -> displayName
    )
}

case class Binding(template: Template,
                   lang: Option[String],
                   baseUri: Option[URL],
                   branding: Option[Branding],
                   addImageQuery: Boolean,
                   contentId: Option[String],
                   displayName: Option[String],
                   texts: Seq[WnsText],
                   images: Seq[Image],
                   groups: Seq[Group]) extends Xmlable {
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
      "addImageQuery" -> Option(addImageQuery),
      "contentId" -> contentId,
      "displayName" -> displayName
    )
}

case class Image(src: String,
                 placement: Option[ImagePlacement],
                 alt: Option[String],
                 addImageQuery: Boolean,
                 hintCrop: Option[HintCrop],
                 hintRemoveMargin: Boolean,
                 hintAlign: Option[HintAlign]) extends Xmlable {
  override def xml: Elem = <image/>.withAttributes(
    "src" -> Option(src),
    "placement" -> placement,
    "alt" -> alt,
    "addImageQuery" -> Option(addImageQuery),
    "hintCrop" -> hintCrop,
    "hintRemoveMargin" -> Option(hintRemoveMargin),
    "hintAlign" -> hintAlign
    )
}

case class WnsText(text: String,
                   lang: Option[String],
                   hintStyle: Option[String],
                   hintWrap: Boolean,
                   hintMaxLines: Option[Int],
                   hintMinLines: Option[Int],
                   hintAlign: Option[HintAlign]) extends Xmlable {
  def xml: Elem =
    <text>{text}</text>.withAttributes(
      "lang" -> lang,
      "hintStyle" -> hintStyle,
      "hintWrap" -> Option(hintWrap),
      "hintMaxLines" -> hintMaxLines,
      "hintMinLines" -> hintMinLines,
      "hintAlign" -> hintAlign
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
      "hintWeight" -> hintWeight,
      "hintTextStacking" -> hintTextStacking
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
