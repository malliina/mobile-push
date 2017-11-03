package com.malliina.http

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.net.ssl.{SSLSocketFactory, X509TrustManager}

import okhttp3._
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConverters.seqAsJavaList
import scala.concurrent.{Future, Promise}

object OkClient {
  val jsonMediaType: MediaType = MediaType.parse("application/json")

  def default: OkClient = apply(okHttpClient)

  def ssl(ssf: SSLSocketFactory, tm: X509TrustManager): OkClient =
    apply(sslClient(ssf, tm))

  def apply(client: OkHttpClient): OkClient = new OkClient(client)

  def okHttpClient: OkHttpClient = new OkHttpClient.Builder()
    .protocols(seqAsJavaList(List(Protocol.HTTP_2, Protocol.HTTP_1_1)))
    .build()

  def sslClient(ssf: SSLSocketFactory, tm: X509TrustManager): OkHttpClient =
    new OkHttpClient.Builder()
      .sslSocketFactory(ssf, tm)
      .protocols(seqAsJavaList(List(Protocol.HTTP_2, Protocol.HTTP_1_1)))
      .build()
}

class OkClient(val client: OkHttpClient) {
  def get(url: FullUrl): Future[Response] = {
    val req = new Request.Builder().url(url.url).get().build()
    execute(req)
  }

  def postJson(url: FullUrl,
               json: JsValue,
               headers: Map[String, String] = Map.empty): Future[Response] =
    post(url, RequestBody.create(OkClient.jsonMediaType, Json.stringify(json)), Map.empty)

  def postFile(url: FullUrl,
               mediaType: MediaType,
               file: Path,
               headers: Map[String, String] = Map.empty): Future[Response] =
    post(url, RequestBody.create(mediaType, file.toFile), headers)

  def postForm(url: FullUrl,
               form: Map[String, String],
               headers: Map[String, String] = Map.empty): Future[Response] = {
    val bodyBuilder = new FormBody.Builder(StandardCharsets.UTF_8)
    form foreach { case (k, v) =>
      bodyBuilder.add(k, v)
    }
    post(url, bodyBuilder.build(), headers)
  }

  def post(url: FullUrl, body: RequestBody, headers: Map[String, String]): Future[Response] = {
    val builder = new Request.Builder().url(url.url).post(body)
    headers.foreach { case (k, v) =>
      builder.header(k, v)
    }
    execute(builder.build())
  }

  def execute(request: Request): Future[Response] = {
    val (future, callback) = PromisingCallback.paired()
    client.newCall(request).enqueue(callback)
    future
  }
}

class PromisingCallback(p: Promise[Response]) extends Callback {
  override def onFailure(call: Call, e: IOException): Unit = p.tryFailure(e)

  override def onResponse(call: Call, response: Response): Unit = p.trySuccess(response)
}

object PromisingCallback {
  def paired() = {
    val p = Promise[Response]()
    val callback = new PromisingCallback(p)
    (p.future, callback)
  }
}
