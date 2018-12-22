package com.malliina.push.adm

import com.malliina.http.{FullUrl, HttpResponse}
import com.malliina.push.Execution.cached
import com.malliina.push.Headers._
import com.malliina.push.OAuthKeys._
import com.malliina.push.adm.ADMClient._
import com.malliina.push.android.AndroidMessage
import com.malliina.push.{AsyncHttp, PushClient, PushException}
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object ADMClient {
  val MessagingPush = "messaging:push"
  val AccessToken = "access_token"

  val AmazonTypeVersion = "X-Amzn-Type-Version"
  val AmazonTypeVersionValue = "com.amazon.device.messaging.ADMMessage@1.0"
  val AmazonAcceptType = "X-Amzn-Accept-Type"
  val AmazonAcceptTypeValue = "com.amazon.device.messaging.ADMSendResult@1.0"

  def apply(clientID: String, clientSecret: String): ADMClient =
    new ADMClient(clientID, clientSecret)
}

class ADMClient(val clientID: String, val clientSecret: String)
  extends PushClient[ADMToken, AndroidMessage, HttpResponse] {

  def send(id: ADMToken, data: Map[String, String]): Future[HttpResponse] =
    push(id, AndroidMessage(data, expiresAfter = 60.seconds))

  def push(id: ADMToken, message: AndroidMessage): Future[HttpResponse] = {
    val body = Json.toJson(message)
    token(clientID, clientSecret).flatMap { t =>
      val headers = Map(
        Authorization -> s"Bearer $t",
        AmazonTypeVersion -> AmazonTypeVersionValue,
        AmazonAcceptType -> AmazonAcceptTypeValue,
        ContentType -> JsonType
      )
      AsyncHttp.withClient { client =>
        client.postJson(FullUrl.https("api.amazon.com", s"/messaging/registrations/${id.token}/messages"), body, headers)
      }
    }
  }

  override def pushAll(ids: Seq[ADMToken], message: AndroidMessage): Future[Seq[HttpResponse]] =
    Future.traverse(ids)(id => push(id, message))

  def token(clientID: String, clientSecret: String): Future[String] =
    accessToken(clientID, clientSecret).map(_.access_token)

  def accessToken: Future[AccessToken] = accessToken(clientID, clientSecret)

  def accessToken(clientID: String, clientSecret: String): Future[AccessToken] =
    tokenRequest(clientID, clientSecret).flatMap { response =>
      response.parse[AccessToken].fold(
        errors => Future.failed[AccessToken](new PushException(s"Invalid JSON in ADM response: $errors")),
        valid => Future.successful(valid)
      )
    }

  private def tokenRequest(clientID: String, clientSecret: String): Future[HttpResponse] = {
    AsyncHttp.withClient { client =>
      val parameters = Map(
        GrantType -> ClientCredentials,
        Scope -> MessagingPush,
        ClientId -> clientID,
        ClientSecret -> clientSecret
      )
      client.postForm(FullUrl.https("api.amazon.com", "/auth/O2/token"), parameters)
    }
  }
}
