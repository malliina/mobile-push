package com.malliina.push.wns

import com.malliina.http._
import com.malliina.push.Headers._
import com.malliina.push.OAuthKeys._
import com.malliina.push._
import com.malliina.push.wns.WNSClient._
import io.circe._

import scala.concurrent.{ExecutionContext, Future}
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

  case class PushMeta(
    client: SimpleHttpClient[Future],
    payload: String,
    headers: Map[String, String]
  )
}

class WNSClient(creds: WNSCredentials, http: SimpleHttpClient[Future])(implicit
  ec: ExecutionContext
) extends PushClient[WNSToken, WNSMessage, WNSResponse] {
  def push(token: WNSToken, message: WNSMessage): Future[WNSResponse] =
    withUrls(message) { meta => pushSingle(meta.client, token, meta.payload, meta.headers) }

  override def pushAll(urls: Seq[WNSToken], message: WNSMessage): Future[Seq[WNSResponse]] =
    withUrls(message) { meta =>
      Future.traverse(urls) { url => pushSingle(meta.client, url, meta.payload, meta.headers) }
    }

  private def withUrls[T](message: WNSMessage)(code: PushMeta => Future[T]): Future[T] =
    fetchAccessToken(http).flatMap { accessToken =>
      val contentType = if (message.notification.isRaw) OctetStream else TextHtml
      val allHeaders = message.headers ++ Map(
        Authorization -> s"Bearer ${accessToken.access_token}",
        ContentType -> contentType,
        RequestStatus -> "true"
      )
      code(PushMeta(http, message.payload, allHeaders))
    }

  def pushSingle(
    client: SimpleHttpClient[Future],
    token: WNSToken,
    body: String,
    headers: Map[String, String]
  ): Future[WNSResponse] = {
    client
      .postString(FullUrl.build(token.token).toOption.get, body, XmlMediaType, headers)
      .map(WNSResponse.fromResponse)
  }

  def fetchAccessToken(client: SimpleHttpClient[Future]): Future[WNSAccessToken] = {
    val parameters = Map(
      GrantType -> ClientCredentials,
      ClientId -> creds.packageSID,
      ClientSecret -> creds.clientSecret,
      Scope -> NotificationHost
    )
    val url = FullUrl.https("login.live.com", "/accesstoken.srf")
    val response = client.postForm(url, Map(ContentType -> FormType), parameters)
    response.flatMap(r => Future.fromTry(parseResponse[WNSAccessToken](r, url)))
  }

  def parseResponse[T: Decoder](response: HttpResponse, url: FullUrl): Try[T] =
    if (response.code == 200) {
      response
        .parse[T]
        .fold(
          invalid => Failure(new JsonException(response.asString, invalid.toString)),
          valid => Success(valid)
        )
    } else {
      Failure(new com.malliina.push.ResponseException(response, url))
    }
}
