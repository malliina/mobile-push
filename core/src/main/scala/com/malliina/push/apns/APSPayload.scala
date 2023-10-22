package com.malliina.push.apns

import com.malliina.push.apns.APSPayload.CriticalSound
import io.circe.{Codec, Decoder, Encoder, Json}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder}
import io.circe.syntax.EncoderOps

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
  staleDate: Option[Long] = None,
  contentState: Option[Json] = None,
  timestamp: Option[Long] = None,
  event: Option[String] = None,
  dismissalDate: Option[Long] = None
)

object APSPayload {
  val Alert = "alert"
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
