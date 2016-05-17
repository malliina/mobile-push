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

class WNSClient(creds: WNSCredentials) extends WindowsClient[WNSToken, WNSMessage] {

  def push(token: WNSToken, message: WNSMessage): Future[Response] = {
    fetchAccessToken() flatMap { accessToken =>
      val allHeaders = message.headers ++ Map(
        Authorization -> s"Bearer ${accessToken.access_token}",
        ContentType -> TextHtml,
        RequestStatus -> "true"
      )
      send(token, message.xml, allHeaders)
    }
  }

  def fetchAccessToken(): Future[WNSAccessToken] = {
    val response = AsyncHttp.execute(client => {
      client
        .post("https://login.live.com/accesstoken.srf", body = "")
        .addParameters(
          GrantType -> ClientCredentials,
          ClientId -> creds.packageSID,
          ClientSecret -> creds.clientSecret,
          Scope -> NotificationHost
        )
    }, Map(ContentType -> WwwFormUrlEncoded))
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

}

object WNSClient {
  val NotificationHost = "notify.windows.com"
  val WnsType = "X-WNS-Type"
  val CachePolicy = "X-WNS-Cache-Policy"
  val RequestStatus = "X-WNS-RequestForStatus"
  val Tag = "X-WNS-Tag"
  val Ttl = "X-WNS-TTL"
  val SuppressPopup = "X-WNS-SuppressPopup"
  val Group = "X-WNS-Group"
  val Match = "X-WNS-Match"
}
