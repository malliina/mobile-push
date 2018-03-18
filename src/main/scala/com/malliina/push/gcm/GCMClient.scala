package com.malliina.push.gcm

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.AsyncHttp.Authorization
import com.malliina.http.{AsyncHttp, FullUrl, WebResponse}
import com.malliina.push.gcm.GCMClient._
import com.malliina.push.{PushClient, ResponseException}
import play.api.libs.json.Json

import scala.concurrent.Future

class GCMClient(val apiKey: String) extends PushClient[GCMToken, GCMMessage, MappedGCMResponse] {
  def push(id: GCMToken, message: GCMMessage) = sendLimitedMapped(Seq(id), message)

  def pushAll(ids: Seq[GCMToken], message: GCMMessage): Future[Seq[MappedGCMResponse]] = {
    val batches = ids.grouped(MAX_RECIPIENTS_PER_REQUEST).toSeq
    val responses = batches.map(batch => sendLimitedMapped(batch, message))
    Future sequence responses
  }

  def send(id: GCMToken, data: Map[String, String]): Future[WebResponse] =
    send(GCMLetter(Seq(id), data))

  private def sendLimitedMapped(ids: Seq[GCMToken], message: GCMMessage): Future[MappedGCMResponse] =
    sendLimited(ids, message).map { r =>
      MappedGCMResponse(ids, parseOrFail(r))
    }

  private def sendLimited(ids: Seq[GCMToken], message: GCMMessage): Future[WebResponse] =
    send(message.toLetter(ids))

  private def send(message: GCMLetter): Future[WebResponse] = {
    val body = Json toJson message
    AsyncHttp.postJson(POST_URL, body, Map(Authorization -> s"key=$apiKey"))
  }
}

object GCMClient {
  //  val POST_URL = "https://android.googleapis.com/gcm/send"
  val POST_URL = FullUrl.https("gcm-http.googleapis.com", "/gcm/send")
  val REGISTRATION_IDS = "registration_ids"
  val DATA = "data"
  val TIME_TO_LIVE = "time_to_live"
  val MAX_RECIPIENTS_PER_REQUEST = 1000

  def parseOrFail(response: WebResponse): GCMResponse =
    if (response.code == 200) {
      response.parse[GCMResponse].get
    } else {
      throw new ResponseException(response)
    }
}
