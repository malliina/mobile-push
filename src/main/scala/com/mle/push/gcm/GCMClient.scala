package com.mle.push.gcm

import com.mle.concurrent.ExecutionContexts.cached
import com.mle.http.AsyncHttp
import com.mle.http.AsyncHttp._
import com.mle.push.PushClient
import com.mle.push.android.AndroidMessage
import com.mle.push.gcm.GCMClient._
import com.ning.http.client.Response
import play.api.libs.json.Json

import scala.concurrent.Future

/**
 *
 * @author mle
 */
class GCMClient(val apiKey: String) extends PushClient[AndroidMessage, Response] {
  def push(id: String, message: AndroidMessage) = send(GCMMessage(Seq(id), message.data, message.expiresAfter))

  def pushAll(ids: Seq[String], message: AndroidMessage): Future[Seq[Response]] = {
    val batches = ids.sliding(MAX_RECIPIENTS_PER_REQUEST).toSeq
    Future sequence batches.map(batch => {
      send(GCMMessage(batch, message.data, message.expiresAfter))
    })
  }

  def send(id: String, data: Map[String, String]): Future[Response] = send(GCMMessage(Seq(id), data))

  def send(message: GCMMessage): Future[Response] = {
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
}
