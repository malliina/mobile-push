package com.malliina.push.apns

import java.nio.file.Path
import java.security.KeyStore
import com.malliina.http.{
  FullUrl,
  HttpClient,
  HttpResponse,
  OkClient,
  OkHttpResponse,
  SimpleHttpClient
}
import com.malliina.push.apns.APNSHttpClient._
import com.malliina.push.{PushClientF, TLSUtils}

import javax.net.ssl.SSLSocketFactory
import okhttp3._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import io.circe.parser.decode

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object APNSHttpClient {
  val DevHost: FullUrl = FullUrl.https("api.sandbox.push.apple.com", "")
  val ProdHost: FullUrl = FullUrl.https("api.push.apple.com", "")

  val ApnsId = "apns-id"
  val ApnsExpiration = "apns-expiration"
  val ApnsPushType = "apns-push-type"
  val ApnsPriority = "apns-priority"
  val ApnsTopic = "apns-topic"
  val ContentLength = "content-length"

  val UTF8 = "UTF-8"

  def apply(socketFactory: SSLSocketFactory, isSandbox: Boolean = false): APNSHttpClient =
    new APNSCertClient(socketFactory, isSandbox)

  def apply(keyStore: KeyStore, keyStorePass: String, isSandbox: Boolean): APNSHttpClient =
    apply(TLSUtils.buildSSLContext(keyStore, keyStorePass).getSocketFactory, isSandbox)

  def fromCert(
    cert: Path,
    keyStorePass: String,
    keyStoreType: String,
    isSandbox: Boolean
  ): Try[APNSHttpClient] =
    TLSUtils
      .loadContext(cert, keyStorePass, keyStoreType)
      .map(ctx => apply(ctx.getSocketFactory, isSandbox))

  def fold(result: Either[APNSError, APNSIdentifier], token: APNSToken): APNSHttpResult =
    result.fold(
      err => APNSHttpResult(token, None, Option(err)),
      id => APNSHttpResult(token, Option(id), None)
    )
}

abstract class APNSHttpClientBase[F[_]](
  http: SimpleHttpClient[F],
  prep: TokenBuilder,
  isSandbox: Boolean
) extends PushClientF[APNSToken, APNSRequest, Either[APNSError, APNSIdentifier], F] {
  val host: FullUrl = if (isSandbox) DevHost else ProdHost
  val jsonMediaType: MediaType = MediaType.parse("application/json")

  def send(id: APNSToken, message: APNSRequest): F[HttpResponse] = {
    val meta = message.meta
    val bodyAsString = message.message.asJson.toString
    val body = RequestBody.create(bodyAsString, jsonMediaType)
    val contentLength = bodyAsString.getBytes(UTF8).length
    val headers = makeHeaders(meta, Instant.now()) ++ Map(ContentLength -> s"$contentLength")
    println(s"Sending $headers")
    http.postJson(
      url(id),
      message.message.asJson,
      headers
    )
  }

  def makeHeaders(meta: APNSMeta, now: Instant): Map[String, String] = {
    val basic = Map(
      ApnsExpiration -> s"${meta.apnsExpiration}",
      ApnsPriority -> s"${meta.apnsPriority.priority}",
      ApnsPushType -> meta.apnsPushType.name,
      ApnsTopic -> meta.apnsTopic.topic
    )
    val id = meta.apnsId.map(apnsId => Map(ApnsId -> apnsId.id)).getOrElse(Map.empty)
    basic ++ id ++ Map("authorization" -> prep.tokenHeader(now))
  }

  def url(token: APNSToken): FullUrl = host / s"/3/device/${token.token}"

  def parseResponse(response: HttpResponse): Either[APNSError, APNSIdentifier] = {
    if (response.code == 200) {
      val apnsId = response.headers.get(ApnsId).flatMap(_.headOption).map(APNSIdentifier.apply)
      apnsId.map(Right.apply).getOrElse(Left(UnknownReason))
    } else {
      val json = decode[APNSErrorJson](response.asString)
      val maybeReason = json.map(_.reason)
      Left(maybeReason getOrElse UnknownReason)
    }
  }
}

/** APNs client, using the HTTP/2 notification API.
  *
  * Uses OkHttp with Jetty's "alpn-boot" in the bootclasspath for HTTP/2 support; please check the
  * build definition of this project for details.
  *
  * @see
  *   https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/CommunicatingwithAPNs.html
  * @see
  *   https://groups.google.com/forum/embed/#!topic/simple-build-tool/TpImNLs1akQ
  * @see
  *   https://github.com/square/okhttp/wiki/Building
  */
class APNSHttpClient(val client: OkClient, prep: TokenBuilder, isSandbox: Boolean = false)
  extends APNSHttpClientBase[Future](client, prep, isSandbox) {
  implicit val ec: ExecutionContext = client.exec

  def pushOne(id: APNSToken, message: APNSRequest): Future[APNSHttpResult] =
    push(id, message).map(r => fold(r, id))

  override def push(
    id: APNSToken,
    message: APNSRequest
  ): Future[Either[APNSError, APNSIdentifier]] =
    send(id, message).map(parseResponse)

  override def pushAll(
    ids: Seq[APNSToken],
    message: APNSRequest
  ): Future[Seq[Either[APNSError, APNSIdentifier]]] =
    Future.traverse(ids)(push(_, message))
}
