package com.malliina.push.gcm

import com.malliina.http.{FullUrl, HttpResponse}
import com.malliina.push.Execution.cached
import com.malliina.push.Headers._
import com.malliina.push.gcm.GCMClient._
import com.malliina.push.{AsyncHttp, PushClient, ResponseException}
import play.api.libs.json.Json

import scala.concurrent.Future

class GCMClient(apiKey: String) extends GoogleClient(apiKey, GcmEndpoint)

object GCMClient {
  val GcmEndpoint = FullUrl.https("gcm-http.googleapis.com", "/gcm/send")
  val RegistrationIds = "registration_ids"
  val Data = "data"
  val TimeToLive = "time_to_live"
  val MaxRecipientsPerRequest = 1000

  def apply(apiKey: String): GCMClient = new GCMClient(apiKey)

  def parseOrFail(response: HttpResponse): GCMResponse =
    if (response.code == 200) {
      response.parse[GCMResponse].toOption.get
    } else {
      throw new ResponseException(response)
    }
}

class GoogleClient(val apiKey: String, postEndpoint: FullUrl)
  extends PushClient[GCMToken, GCMMessage, MappedGCMResponse] {

  def push(id: GCMToken, message: GCMMessage) = sendLimitedMapped(Seq(id), message)

  def pushAll(ids: Seq[GCMToken], message: GCMMessage): Future[Seq[MappedGCMResponse]] = {
    val batches = ids.grouped(MaxRecipientsPerRequest).toSeq
    val responses = batches.map(batch => sendLimitedMapped(batch, message))
    Future sequence responses
  }

  def send(id: GCMToken, data: Map[String, String]): Future[HttpResponse] =
    send(GCMLetter(Seq(id), data))

  private def sendLimitedMapped(ids: Seq[GCMToken], message: GCMMessage): Future[MappedGCMResponse] =
    sendLimited(ids, message).map { r =>
      MappedGCMResponse(ids, parseOrFail(r))
    }

  private def sendLimited(ids: Seq[GCMToken], message: GCMMessage): Future[HttpResponse] =
    send(message.toLetter(ids))

  private def send(message: GCMLetter): Future[HttpResponse] = {
    val body = Json.toJson(message)
    AsyncHttp.withClient(_.postJson(GcmEndpoint, body, Map(Authorization -> s"key=$apiKey")))
  }
}
