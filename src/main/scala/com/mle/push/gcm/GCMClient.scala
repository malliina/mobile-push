package com.mle.push.gcm

import com.mle.concurrent.ExecutionContexts.cached
import com.mle.http.AsyncHttp
import com.mle.http.AsyncHttp._
import com.mle.push.gcm.GCMClient._
import com.mle.push.{PushClient, ResponseException}
import com.ning.http.client.Response
import play.api.libs.json.Json

import scala.concurrent.Future

/**
 *
 * @author mle
 */
class GCMClient(val apiKey: String) extends PushClient[GCMMessage, MappedGCMResponse] {
  def push(id: String, message: GCMMessage) = sendLimitedMapped(Seq(id), message)

  def pushAll(ids: Seq[String], message: GCMMessage): Future[Seq[MappedGCMResponse]] = {
    val batches = ids.grouped(MAX_RECIPIENTS_PER_REQUEST).toSeq
    val responses = batches.map(batch => sendLimitedMapped(batch, message))
    Future sequence responses
  }

  def send(id: String, data: Map[String, String]): Future[Response] = send(GCMLetter(Seq(id), data))

  private def sendLimitedMapped(ids: Seq[String], message: GCMMessage): Future[MappedGCMResponse] =
    sendLimited(ids, message).map(r => MappedGCMResponse(ids, parseOrFail(r)))

  private def sendLimited(ids: Seq[String], message: GCMMessage): Future[Response] = send(message.toLetter(ids))

  private def send(message: GCMLetter): Future[Response] = {
    val body = Json toJson message
    AsyncHttp.postJson(POST_URL, body, Map(AUTHORIZATION -> s"key=$apiKey"))
  }
}

object GCMClient {
  val POST_URL = "https://android.googleapis.com/gcm/send"
  val REGISTRATION_IDS = "registration_ids"
  val DATA = "data"
  val TIME_TO_LIVE = "time_to_live"
  val MAX_RECIPIENTS_PER_REQUEST = 1000

  def parseOrFail(response: Response): GCMResponse =
    if (response.getStatusCode == 200) {
      Json.parse(response.getResponseBody).as[GCMResponse]
    } else {
      throw new ResponseException(response)
    }
}
