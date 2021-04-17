package com.malliina.push.gcm

import com.malliina.http.{FullUrl, HttpClient, HttpResponse}
import com.malliina.push.Headers._
import com.malliina.push.gcm.GCMClient._
import com.malliina.push.{JsonException, PushClient, PushClientF, ResponseException}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class GCMClient(apiKey: String, http: HttpClient[Future], ec: ExecutionContext)
  extends GoogleClient(apiKey, GcmEndpoint, http)(ec)

object GCMClient {
  val GcmEndpoint = FullUrl.https("gcm-http.googleapis.com", "/gcm/send")
  val RegistrationIds = "registration_ids"
  val Data = "data"
  val TimeToLive = "time_to_live"
  val MaxRecipientsPerRequest = 1000

  def apply(apiKey: String, http: HttpClient[Future], ec: ExecutionContext): GCMClient =
    new GCMClient(apiKey, http, ec)

  def parseOrFail(response: HttpResponse): GCMResponse =
    if (response.code == 200) {
      response
        .parse[GCMResponse]
        .fold(err => throw new JsonException(response.asString, err), identity)
    } else {
      throw new ResponseException(response)
    }
}

abstract class GoogleClientBase[F[+_]](
  val apiKey: String,
  postEndpoint: FullUrl,
  http: HttpClient[F]
) extends PushClientF[GCMToken, GCMMessage, MappedGCMResponse, F] {

  def send(id: GCMToken, data: Map[String, String]): F[HttpResponse] =
    send(GCMLetter(Seq(id), data))

  protected def sendLimited(ids: Seq[GCMToken], message: GCMMessage): F[HttpResponse] =
    send(message.toLetter(ids))

  protected def send(message: GCMLetter): F[HttpResponse] = {
    val body = Json.toJson(message)
    http.postJson(postEndpoint, body, Map(Authorization -> s"key=$apiKey"))
  }
}

class GoogleClient(
  apiKey: String,
  postEndpoint: FullUrl,
  http: HttpClient[Future]
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
    sendLimited(ids, message).map { r => MappedGCMResponse(ids, parseOrFail(r)) }
}
