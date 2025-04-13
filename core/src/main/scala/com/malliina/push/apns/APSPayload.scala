package com.malliina.push.apns

import com.malliina.push.apns.APSPayload.{APSEvent, CriticalSound}
import io.circe.{Codec, Decoder, Encoder, Json}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder}
import io.circe.syntax.EncoderOps

import java.time.Instant
import scala.util.Try

/** @param alert
  *   Some(Left(...)) for a simple alert text, Some(Right(...)) for more verbose alert details, None
  *   for background notifications
  * @param badge
  *   badge number
  * @param sound
  *   rock.mp3
  *
  * @see
  *   https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/generating_a_remote_notification
  */
case class APSPayload(
  alert: Option[Either[String, AlertPayload]],
  badge: Option[Int] = None,
  sound: Option[Either[String, CriticalSound]] = None,
  category: Option[String] = None,
  threadId: Option[String] = None,
  mutableContent: Option[Int] = None,
  targetContentId: Option[String] = None,
  interruptionLevel: Option[String] = None,
  relevanceScore: Option[String] = None,
  filterCriteria: Option[String] = None,
  staleDate: Option[Instant] = None,
  contentState: Option[Json] = None,
  timestamp: Option[Instant] = None,
  event: Option[APSEvent] = None,
  dismissalDate: Option[Instant] = None,
  attributes: Option[Json] = None,
  attributesType: Option[String] = None
)

object APSPayload {
  implicit val timestampCodec: Codec[Instant] = Codec.from(
    Decoder.decodeLong.emapTry(l => Try(Instant.ofEpochSecond(l))),
    Encoder.encodeLong.contramap(_.getEpochSecond)
  )
  val Alert = "alert"
  private val Attributes = "attributes"
  private val AttributesType = "attributes-type"
  val Badge = "badge"
  val Category = "category"
  val ContentAvailable = "content-available"
  private val ContentState = "content-state"
  private val DismissalDate = "dismissal-date"
  private val Event = "event"
  private val FilterCriteria = "filter-criteria"
  private val InterruptionLevel = "interruption-level"
  private val MutableContent = "mutable-content"
  private val RelevanceScore = "relevance-score"
  val Sound = "sound"
  private val StaleDate = "stale-date"
  private val TargetContentId = "target-content-id"
  val ThreadId = "thread-id"
  private val Timestamp = "timestamp"

  case class CriticalSound(critical: Int, name: String, volume: Int)

  object CriticalSound {
    implicit val json: Codec[CriticalSound] = deriveCodec[CriticalSound]
  }

  sealed abstract class APSEvent(val name: String)
  object APSEvent {
    case object Start extends APSEvent("start")
    case object Update extends APSEvent("update")
    case object End extends APSEvent("end")
    case class Other(n: String) extends APSEvent(n)

    implicit val json: Codec[APSEvent] = Codec.from(
      Decoder.decodeString.map(s => Seq(Start, Update, End).find(_.name == s).getOrElse(Other(s))),
      Encoder.encodeString.contramap(_.name)
    )
  }

  implicit val af: Codec[Either[String, AlertPayload]] = Codec.from(
    eitherDecoder[String, AlertPayload],
    eitherEncoder[String, AlertPayload]
  )
  implicit val payloadEncoder: Encoder[APSPayload] = (p: APSPayload) => {
    val alertJson = p.alert.fold(Json.obj(ContentAvailable -> Json.fromInt(1))) { e =>
      Json.obj(Alert -> e.asJson)
    }
    alertJson.deepMerge(
      objectify(Badge, p.badge)
        .deepMerge(objectify(Sound, p.sound))
        .deepMerge(objectify(Category, p.category))
        .deepMerge(objectify(TargetContentId, p.targetContentId))
        .deepMerge(objectify(ThreadId, p.threadId))
        .deepMerge(objectify(InterruptionLevel, p.interruptionLevel))
        .deepMerge(objectify(RelevanceScore, p.relevanceScore))
        .deepMerge(objectify(FilterCriteria, p.filterCriteria))
        .deepMerge(objectify(StaleDate, p.staleDate))
        .deepMerge(objectify(ContentState, p.contentState))
        .deepMerge(objectify(Timestamp, p.timestamp))
        .deepMerge(objectify(Event, p.event))
        .deepMerge(objectify(DismissalDate, p.dismissalDate))
        .deepMerge(objectify(Attributes, p.attributes))
        .deepMerge(objectify(AttributesType, p.attributesType))
    )
  }

  implicit val json: Codec[APSPayload] = Codec.from(
    deriveDecoder[APSPayload],
    payloadEncoder
  )

  def full(
    payload: AlertPayload,
    badge: Option[Int] = None,
    sound: Option[String] = None,
    category: Option[String] = None
  ): APSPayload =
    apply(Option(Right(payload)), badge, sound.map(Left.apply), category)

  def simple(
    text: String,
    badge: Option[Int] = None,
    sound: Option[String] = None,
    category: Option[String] = None
  ): APSPayload =
    apply(Option(Left(text)), badge, sound.map(Left.apply), category)

  def background(
    badge: Option[Int] = None,
    sound: Option[String] = None,
    category: Option[String] = None
  ): APSPayload = apply(None, badge, sound.map(Left.apply), category)

  // https://developer.apple.com/documentation/activitykit/starting-and-updating-live-activities-with-activitykit-push-notifications#Construct-the-ActivityKit-remote-push-notification-payload
  def startLiveActivity[A: Encoder, C: Encoder](
    now: Instant,
    attributesType: String,
    attributes: A,
    contentState: C,
    alert: Either[String, AlertPayload],
    dismissalDate: Option[Instant]
  ): APSPayload =
    apply(
      alert = Option(alert),
      attributesType = Option(attributesType),
      attributes = Option(attributes.asJson),
      contentState = Option(contentState.asJson),
      timestamp = Option(now),
      event = Option(APSEvent.Start)
    )

  def updateLiveActivity[C: Encoder](
    now: Instant,
    contentState: C,
    alert: Option[Either[String, AlertPayload]],
    staleDate: Option[Instant],
    dismissalDate: Option[Instant]
  ) = liveActivity(now, contentState, APSEvent.Update, alert, staleDate, dismissalDate)

  def endLiveActivity[C: Encoder](
    now: Instant,
    contentState: C,
    dismissalDate: Option[Instant]
  ): APSPayload =
    liveActivity(now, contentState, APSEvent.End, None, dismissalDate, None)

  private def liveActivity[C: Encoder](
    now: Instant,
    contentState: C,
    event: APSEvent,
    alert: Option[Either[String, AlertPayload]],
    staleDate: Option[Instant],
    dismissalDate: Option[Instant]
  ): APSPayload =
    apply(
      alert = alert,
      contentState = Option(contentState.asJson),
      timestamp = Option(now),
      staleDate = staleDate,
      dismissalDate = dismissalDate,
      event = Option(event)
    )

  implicit def eitherDecoder[A, B](implicit a: Decoder[A], b: Decoder[B]): Decoder[Either[A, B]] = {
    val left: Decoder[Either[A, B]] = a.map(Left.apply)
    val right: Decoder[Either[A, B]] = b.map(Right.apply)
    left or right
  }

  implicit def eitherEncoder[A: Encoder, B: Encoder]: Encoder[Either[A, B]] =
    (o: Either[A, B]) => o.fold(_.asJson, _.asJson)

  private def objectify[T: Encoder](key: String, opt: Option[T]): Json =
    opt.fold(Json.obj())(v => Json.obj(key -> v.asJson))
}
