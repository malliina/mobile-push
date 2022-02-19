package com.malliina.push.apns

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

/** @param alert Some(Left(...)) for a simple alert text, Some(Right(...)) for more verbose alert details, None for background notifications
  * @param badge badge number
  * @param sound rock.mp3
  */
case class APSPayload(
  alert: Option[Either[String, AlertPayload]],
  badge: Option[Int] = None,
  sound: Option[String] = None,
  category: Option[String] = None,
  threadId: Option[String] = None
)

object APSPayload {
  val Alert = "alert"
  val Badge = "badge"
  val Category = "category"
  val ContentAvailable = "content-available"
  val Sound = "sound"
  val ThreadId = "thread-id"

  implicit val af: Codec[Either[String, AlertPayload]] = Codec.from(
    eitherDecoder[String, AlertPayload],
    eitherEncoder[String, AlertPayload]
  )
  implicit val payloadEncoder: Encoder[APSPayload] = new Encoder[APSPayload] {
    final def apply(p: APSPayload): Json = {
      val alertJson = p.alert.fold(Json.obj(ContentAvailable -> Json.fromInt(1))) { e =>
        Json.obj(Alert -> e.asJson)
      }
      alertJson.deepMerge(
        objectify(Badge, p.badge)
          .deepMerge(objectify(Sound, p.sound))
          .deepMerge(objectify(Category, p.category))
          .deepMerge(objectify(ThreadId, p.threadId))
      )
    }
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
    apply(Option(Right(payload)), badge, sound, category)

  def simple(
    text: String,
    badge: Option[Int] = None,
    sound: Option[String] = None,
    category: Option[String] = None
  ): APSPayload =
    apply(Option(Left(text)), badge, sound, category)

  def background(
    badge: Option[Int] = None,
    sound: Option[String] = None,
    category: Option[String] = None
  ): APSPayload = apply(None, badge, sound, category)

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
