package com.malliina.push.wns

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.AsyncHttp
import com.malliina.http.AsyncHttp._
import com.malliina.push.Headers.TextHtml
import com.malliina.push.OAuthKeys._
import com.malliina.push._
import com.malliina.push.wns.WNSClient._
import org.asynchttpclient.Response
import play.api.libs.json.{JsError, Json, Reads}

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
        val allHeaders = message.headers ++ Map(
          Authorization -> s"Bearer ${accessToken.access_token}",
          ContentType -> TextHtml,
          RequestStatus -> "true"
        )
        val payload = WindowsClient.serialize(message.xml)
        code(PushMeta(client, payload, allHeaders))
      }
    }

  def pushSingle(client: AsyncHttp,
                 token: WNSToken,
                 body: String,
                 headers: Map[String, String]): Future[WNSResponse] = {
    val request = client.post(token.token, body)
    headers.foreach(p => request.setHeader(p._1, p._2))
    request.run().map(WNSResponse.fromResponse)
  }

  def fetchAccessToken(client: AsyncHttp): Future[WNSAccessToken] = {
    val request = client
      .post("https://login.live.com/accesstoken.srf", body = "")
      .addParameters(
        GrantType -> ClientCredentials,
        ClientId -> creds.packageSID,
        ClientSecret -> creds.clientSecret,
        Scope -> NotificationHost
      )
    request.setHeader(ContentType, WwwFormUrlEncoded)
    val response = request.run()
    response.flatMap(r => Future.fromTry(parseResponse[WNSAccessToken](r)))
  }

  def parseResponse[T: Reads](response: Response): Try[T] = {
    if (response.getStatusCode == 200) {
      val body = response.getResponseBody
      Try(Json.parse(body)).transform(
        json => json.validate[T].fold(
          invalid => Failure(new JsonException(body, JsError(invalid))),
          valid => Success(valid)
        ),
        throwable => Failure(new NotJsonException(body))
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
