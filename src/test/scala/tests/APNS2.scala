package tests

import java.security.KeyStore
import javax.net.ssl.SSLContext

import com.malliina.push.TLSUtils
import com.malliina.push.apns._
import org.scalatest.FunSuite

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class APNS2 extends BaseSuite {
  val testToken = APNSToken.build("6c9969eee832f6ed2a11d04d6daa404db13cc3d97f7298f0c042616fc2a5cc34").get

  test("a successful notifications returns an apns-id") {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = clientCertContext()
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = false)
      val result = await(client.push(testToken, request))
      assert(result.right.toOption.isDefined)
    }
  }

  test("using a development token with the production API returns BadDeviceToken") {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = clientCertContext()
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = false)
      val result = await(client.push(creds.token, request))
      assert(result.left.toOption.contains(BadDeviceToken))
    }
  }

  test("sample code for README") {
    if(false) {
      val certKeyStore: KeyStore = ???
      val certPass: String = ???
      val topic = APNSTopic("org.company.MyApp")
      val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").get
      val message = APNSMessage.simple("Hey, sexy!")
      val request = APNSRequest.withTopic(topic, message)
      val client = APNSHttpClient(certKeyStore, certPass, isSandbox = true)
      val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
    }
  }

  def clientCertContext(): SSLContext = {
    val creds = APNSHttpConf.load
    val pass = creds.pass
    TLSUtils.keyStoreFromFile(creds.file, pass, "PKCS12")
      .map(ks => TLSUtils.buildSSLContext(ks, pass))
      .get
  }
}
