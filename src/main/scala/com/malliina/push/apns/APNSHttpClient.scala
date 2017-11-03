package com.malliina.push.apns

import java.nio.file.Path
import java.security.KeyStore
import javax.net.ssl.SSLSocketFactory

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.OkClient
import com.malliina.push.apns.APNSHttpClient._
import com.malliina.push.{PushClient, TLSUtils}
import okhttp3.{MediaType, _}
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.Try

/** APNs client, using the HTTP/2 notification API.
  *
  * Uses OkHttp with Jetty's "alpn-boot" in the bootclasspath for HTTP/2 support;
  * please check the build definition of this project in project/PushBuild.scala for details.
  *
  * @see https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/CommunicatingwithAPNs.html
  * @see https://groups.google.com/forum/embed/#!topic/simple-build-tool/TpImNLs1akQ
  * @see https://github.com/square/okhttp/wiki/Building
  */
class APNSHttpClient(client: OkClient, isSandbox: Boolean = false)
  extends PushClient[APNSToken, APNSRequest, Either[APNSError, APNSIdentifier]] {

  val host = if (isSandbox) DevHost else ProdHost
  val jsonMediaType = MediaType.parse("application/json")

  override def push(id: APNSToken, message: APNSRequest): Future[Either[APNSError, APNSIdentifier]] =
    send(id, message).map(parseResponse)

  override def pushAll(ids: Seq[APNSToken], message: APNSRequest): Future[Seq[Either[APNSError, APNSIdentifier]]] =
    Future.traverse(ids)(push(_, message))

  def send(id: APNSToken, message: APNSRequest): Future[Response] = {
    val meta = message.meta
    val bodyAsString = Json.stringify(Json.toJson(message.message))
    val body = RequestBody.create(jsonMediaType, bodyAsString)
    val contentLength = bodyAsString.getBytes(UTF8).length
    val request = withHeaders(meta) {
      new Request.Builder()
        .url(url(id))
        .post(body)
        .header(ContentLength, "" + contentLength)
    }.build()
    client.execute(request)
  }

  def withHeaders(meta: APNSMeta)(request: Request.Builder): Request.Builder = {
    val request1 = request
      .header(ApnsExpiration, "" + meta.apnsExpiration)
      .header(ApnsPriority, "" + meta.apnsPriority.priority)
      .header(ApnsTopic, meta.apnsTopic.topic)
    val withDefaults = meta.apnsId.map(id => request1.header(ApnsId, id.id)) getOrElse request1
    installHeaders(withDefaults)
  }

  def installHeaders(request: Request.Builder): Request.Builder = request

  def url(token: APNSToken) = s"$host/3/device/${token.token}"

  def parseResponse(response: Response): Either[APNSError, APNSIdentifier] = {
    if (response.code() == 200) {
      val apnsId = Option(response.header(ApnsId)).map(APNSIdentifier.apply)
      apnsId.map(Right.apply).getOrElse(Left(UnknownReason))
    } else {
      val json = Try(Json.parse(response.body().string()))
      val maybeReason = json.toOption.flatMap(js => (js \ APNSError.ReasonKey).asOpt[APNSError])
      Left(maybeReason getOrElse UnknownReason)
    }
  }
}

object APNSHttpClient {
  val DevHost = "https://api.development.push.apple.com:443"
  val ProdHost = "https://api.push.apple.com:443"

  val ApnsId = "apns-id"
  val ApnsExpiration = "apns-expiration"
  val ApnsPriority = "apns-priority"
  val ApnsTopic = "apns-topic"
  val ContentLength = "content-length"

  val UTF8 = "UTF-8"

  def apply(socketFactory: SSLSocketFactory, isSandbox: Boolean = false): APNSHttpClient =
    new APNSCertClient(socketFactory, isSandbox)

  def apply(keyStore: KeyStore, keyStorePass: String, isSandbox: Boolean): APNSHttpClient =
    apply(TLSUtils.buildSSLContext(keyStore, keyStorePass).getSocketFactory, isSandbox)

  def fromCert(cert: Path, keyStorePass: String, keyStoreType: String, isSandbox: Boolean): Try[APNSHttpClient] =
    TLSUtils.loadContext(cert, keyStorePass, keyStoreType)
      .map(ctx => new APNSCertClient(ctx.getSocketFactory, isSandbox))
}
