package com.malliina.push.wns

import com.malliina.http._
import com.malliina.push.Headers._
import com.malliina.push.OAuthKeys._
import com.malliina.push._
import com.malliina.push.wns.WNSClient._
import okhttp3.RequestBody
import play.api.libs.json.Reads

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object WNSClient {
  val NotificationHost = "notify.windows.com"

  val CachePolicy = "X-WNS-Cache-Policy"
  val Group = "X-WNS-Group"
  val Match = "X-WNS-Match"
  val RequestStatus = "X-WNS-RequestForStatus"
  val SuppressPopup = "X-WNS-SuppressPopup"
  val Tag = "X-WNS-Tag"
  val Ttl = "X-WNS-TTL"
  val WnsType = "X-WNS-Type"
}

class WNSClient(creds: WNSCredentials) extends PushClient[WNSToken, WNSMessage, WNSResponse] {

  case class PushMeta(client: OkClient, payload: String, headers: Map[String, String])

  def push(token: WNSToken, message: WNSMessage): Future[WNSResponse] =
    withUrls(message) { meta =>
      pushSingle(meta.client, token, meta.payload, meta.headers)
    }

  override def pushAll(urls: Seq[WNSToken], message: WNSMessage): Future[Seq[WNSResponse]] =
    withUrls(message) { meta =>
      Future.traverse(urls) { url =>
        pushSingle(meta.client, url, meta.payload, meta.headers)
      }
    }

  private def withUrls[T](message: WNSMessage)(code: PushMeta => Future[T]): Future[T] =
    AsyncHttp.usingAsync(OkClient.default) { client =>
      fetchAccessToken(client) flatMap { accessToken =>
        val contentType = if (message.notification.isRaw) OctetStream else TextHtml
        val allHeaders = message.headers ++ Map(
          Authorization -> s"Bearer ${accessToken.access_token}",
          ContentType -> contentType,
          RequestStatus -> "true"
        )
        code(PushMeta(client, message.payload, allHeaders))
      }
    }

  def pushSingle(
    client: OkClient,
    token: WNSToken,
    body: String,
    headers: Map[String, String]
  ): Future[WNSResponse] = {
    val requestBody = RequestBody.create(body, XmlMediaType)
    client
      .post(FullUrl.build(token.token).toOption.get, requestBody, headers)
      .map(WNSResponse.fromResponse)
  }

  def fetchAccessToken(client: OkClient): Future[WNSAccessToken] = {
    val parameters = Map(
      GrantType -> ClientCredentials,
      ClientId -> creds.packageSID,
      ClientSecret -> creds.clientSecret,
      Scope -> NotificationHost
    )
    val response = client.postForm(
      FullUrl.https("login.live.com", "/accesstoken.srf"),
      Map(ContentType -> FormType),
      parameters
    )
    response.flatMap(r => Future.fromTry(parseResponse[WNSAccessToken](r)))
  }

  def parseResponse[T: Reads](response: HttpResponse): Try[T] = {
    if (response.code == 200) {
      response
        .parse[T]
        .fold(
          invalid => Failure(new JsonException(response.asString, invalid)),
          valid => Success(valid)
        )
    } else {
      Failure(new com.malliina.push.ResponseException(response))
    }
  }
}
