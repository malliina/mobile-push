package com.malliina.push.apns

import com.malliina.http.OkClient
import okhttp3.{OkHttpClient, Protocol}

import java.security.cert.X509Certificate
import javax.net.ssl.{SSLSocketFactory, X509TrustManager}
import scala.jdk.CollectionConverters.SeqHasAsJava

object APNSCertClient {
  val tm: X509TrustManager = new X509TrustManager {
    override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()
    override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = ()
    override def getAcceptedIssuers: Array[X509Certificate] = Array.empty[X509Certificate]
  }

  def httpClient(ssf: SSLSocketFactory): OkHttpClient =
    new OkHttpClient.Builder()
      .sslSocketFactory(ssf, tm)
      .protocols(List(Protocol.HTTP_2, Protocol.HTTP_1_1).asJava)
      .build()
}

class APNSCertClient(socketFactory: SSLSocketFactory, isSandbox: Boolean = false)
  extends APNSHttpClient(OkClient.ssl(socketFactory, APNSCertClient.tm), isSandbox)
  with AutoCloseable {
  override def close(): Unit = client.close()
}
