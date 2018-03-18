package com.malliina.push.wns

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.AsyncHttp._
import com.malliina.http.{AsyncHttp, FullUrl, WebResponse}
import com.malliina.push.Headers.{OctetStream, TextHtml}
import com.malliina.push.OAuthKeys._
import com.malliina.push._
import com.malliina.push.wns.WNSClient._
import play.api.libs.json.{JsError, Reads}

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

  case class PushMeta(client: AsyncHttp, payload: String, headers: Map[String, String])

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
    usingAsync(new AsyncHttp()) { client =>
      fetchAccessToken(client) flatMap { accessToken =>
        val contentType = if (message.notification.isRaw) OctetStream else TextHtml
        val allHeaders = message.headers ++ Map(
          Authorization -> s"Bearer ${accessToken.access_token}",
          ContentTypeHeaderName -> contentType,
          RequestStatus -> "true"
        )
        code(PushMeta(client, message.payload, allHeaders))
      }
    }

  def pushSingle(client: AsyncHttp,
                 token: WNSToken,
                 body: String,
                 headers: Map[String, String]): Future[WNSResponse] = {
    val response = client.postAny(FullUrl.build(token.token).toOption.get, body, org.apache.http.entity.ContentType.APPLICATION_XML, headers)
    response.map(WNSResponse.fromResponse)
  }

  def fetchAccessToken(client: AsyncHttp): Future[WNSAccessToken] = {
    val parameters = Map(
      GrantType -> ClientCredentials,
      ClientId -> creds.packageSID,
      ClientSecret -> creds.clientSecret,
      Scope -> NotificationHost
    )
    val response = AsyncHttp.withClient(_.postEmpty(
      FullUrl.https("login.live.com", "/accesstoken.srf"),
      Map(ContentTypeHeaderName -> WwwFormUrlEncoded),
      parameters
    ))
    response.flatMap(r => Future.fromTry(parseResponse[WNSAccessToken](r)))
  }

  def parseResponse[T: Reads](response: WebResponse): Try[T] = {
    if (response.code == 200) {
      response.parse[T].fold(
        invalid => Failure(new JsonException(response.asString, JsError(invalid))),
        valid => Success(valid)
      )
    } else {
      Failure(new ResponseException(response))
    }
  }

  def usingAsync[T <: AutoCloseable, U](res: T)(code: T => Future[U]): Future[U] = {
    val result = code(res)
    result.onComplete(_ => res.close())
    result
  }
}
