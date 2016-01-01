package com.malliina.push.adm

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.AsyncHttp
import com.malliina.http.AsyncHttp.{RichRequestBuilder, _}
import com.malliina.util.Log
import com.malliina.push.adm.ADMClient._
import com.malliina.push.android.AndroidMessage
import com.malliina.push.{PushClient, PushException}
import com.ning.http.client.Response
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * @author Michael
 */
class ADMClient(val clientID: String, val clientSecret: String)
  extends PushClient[ADMToken, AndroidMessage, Response]
  with Log {

  def send(id: ADMToken, data: Map[String, String]): Future[Response] =
    push(id, AndroidMessage(data, expiresAfter = 60.seconds))

  def push(id: ADMToken, message: AndroidMessage): Future[Response] = {
    val body = Json.toJson(message)
    token(clientID, clientSecret).flatMap(t => {
      AsyncHttp.postJson(s"https://api.amazon.com/messaging/registrations/${id.token}/messages", body, Map(
        AUTHORIZATION -> s"Bearer $t",
        AmazonTypeVersion -> AmazonTypeVersionValue,
        AmazonAcceptType -> AmazonAcceptTypeValue
      ))
    })
  }

  override def pushAll(ids: Seq[ADMToken], message: AndroidMessage): Future[Seq[Response]] =
    Future sequence ids.map(id => push(id, message))

  def token(clientID: String, clientSecret: String): Future[String] =
    accessToken(clientID, clientSecret).map(_.access_token)

  def accessToken: Future[AccessToken] = accessToken(clientID, clientSecret)

  def accessToken(clientID: String, clientSecret: String): Future[AccessToken] =
    tokenRequest(clientID, clientSecret)
      .flatMap(response => Json.parse(response.getResponseBody).validate[AccessToken].fold(
      errors => Future.failed[AccessToken](new PushException(s"Invalid JSON in ADM response: $errors")),
      valid => Future.successful(valid)
    ))

  private def tokenRequest(clientID: String, clientSecret: String): Future[Response] = {
    AsyncHttp.execute(client => {
      client.post("https://api.amazon.com/auth/O2/token", "").addParameters(
        GRANT_TYPE -> CLIENT_CREDENTIALS,
        SCOPE -> MESSAGING_PUSH,
        CLIENT_ID -> clientID,
        CLIENT_SECRET -> clientSecret)
    }, Map(CONTENT_TYPE -> WWW_FORM_URL_ENCODED))
  }
}

object ADMClient {
  val GRANT_TYPE = "grant_type"
  val CLIENT_CREDENTIALS = "client_credentials"
  val SCOPE = "scope"
  val MESSAGING_PUSH = "messaging:push"
  val CLIENT_ID = "client_id"
  val CLIENT_SECRET = "client_secret"
  val ACCESS_TOKEN = "access_token"

  val AmazonTypeVersion = "X-Amzn-Type-Version"
  val AmazonTypeVersionValue = "com.amazon.device.messaging.ADMMessage@1.0"
  val AmazonAcceptType = "X-Amzn-Accept-Type"
  val AmazonAcceptTypeValue = "com.amazon.device.messaging.ADMSendResult@1.0"
}
