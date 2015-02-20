package com.mle.push.gcm

import com.mle.concurrent.ExecutionContexts.cached
import com.mle.http.AsyncHttp
import com.mle.http.AsyncHttp._
import com.mle.push.HttpPushClient
import com.mle.push.android.AndroidMessage
import com.ning.http.client.Response
import play.api.libs.json.Json

import scala.concurrent.Future

/**
 *
 * @author mle
 */
class GCMClient(val apiKey: String) extends HttpPushClient[AndroidMessage] {

  import com.mle.push.gcm.GCMClient._

  def send(id: String, message: AndroidMessage) = send(GCMMessage(Seq(id), message.data, message.expiresAfter))

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
}

