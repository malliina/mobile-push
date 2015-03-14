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
class GCMClient(val apiKey: String) extends PushClient[GCMMessage, Response] {
  def push(id: String, message: GCMMessage) = send(message.toLetter(Seq(id)))

  def pushAllParsed(ids: Seq[String], message: GCMMessage): Future[Seq[GCMResponse]] =
    pushParsed(ids, message).map(_.map(_.response))

  def pushParsed(ids: Seq[String], message: GCMMessage): Future[Seq[MappedGCMResponse]] =
    pushMapped(ids, message).map(_.map(_.parsed))

  def pushAll(ids: Seq[String], message: GCMMessage): Future[Seq[Response]] =
    pushMapped(ids, message).map(_.map(_.response))

  def pushMapped(ids: Seq[String], message: GCMMessage): Future[Seq[MappedResponse]] = {
    val batches = ids.grouped(MAX_RECIPIENTS_PER_REQUEST).toSeq
    val responses = batches.map(batch => send(message.toLetter(batch)).map(r => MappedResponse(batch, r)))
    Future sequence responses
  }

  def send(id: String, data: Map[String, String]): Future[Response] = send(GCMLetter(Seq(id), data))

  def send(message: GCMLetter): Future[Response] = {
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
