package com.malliina.push.gcm

import cats.effect.Async
import com.malliina.http.{FullUrl, HttpClient, HttpResponse, OkHttpHttpClient}
import com.malliina.push.Headers._
import com.malliina.push.gcm.GCMClient._
import com.malliina.push.{JsonException, PushClient, PushClientF, ResponseException}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import scala.concurrent.{ExecutionContext, Future}

object GCMClient {
  val RegistrationIds = "registration_ids"
  val Data = "data"
  val TimeToLive = "time_to_live"
  val MaxRecipientsPerRequest = 1000

  def parseOrFail(response: HttpResponse, url: FullUrl): GCMResponse =
    if (response.code == 200) {
      response
        .parse[GCMResponse]
        .fold(err => throw new JsonException(response.asString, "JSON error."), identity)
    } else {
      throw new ResponseException(response, url)
    }
}

abstract class GoogleClientBase[F[_]](
  val apiKey: String,
  val postEndpoint: FullUrl,
  http: OkHttpHttpClient[F]
) extends PushClientF[GCMToken, GCMMessage, MappedGCMResponse, F] {

  def send(id: GCMToken, data: Map[String, String]): F[HttpResponse] =
    send(GCMLetter(Seq(id), data))

  protected def sendLimited(ids: Seq[GCMToken], message: GCMMessage): F[HttpResponse] =
    send(message.toLetter(ids))

  protected def send(message: GCMLetter): F[HttpResponse] = {
    val res = http.postJson(postEndpoint, message.asJson, Map(Authorization -> s"key=$apiKey"))
    http.flatMap(res)(r => http.success(r))
  }
}

class GoogleClient(
  apiKey: String,
  postEndpoint: FullUrl,
  http: OkHttpHttpClient[Future]
)(implicit ec: ExecutionContext)
  extends GoogleClientBase[Future](apiKey, postEndpoint, http)
  with PushClient[GCMToken, GCMMessage, MappedGCMResponse] {

  def push(id: GCMToken, message: GCMMessage): Future[MappedGCMResponse] =
    sendLimitedMapped(Seq(id), message)

  def pushAll(ids: Seq[GCMToken], message: GCMMessage): Future[Seq[MappedGCMResponse]] = {
    val batches = ids.grouped(MaxRecipientsPerRequest).toSeq
    Future.traverse(batches) { batch => sendLimitedMapped(batch, message) }
  }

  private def sendLimitedMapped(
    ids: Seq[GCMToken],
    message: GCMMessage
  ): Future[MappedGCMResponse] =
    sendLimited(ids, message).map { r => MappedGCMResponse(ids, parseOrFail(r, postEndpoint)) }
}
