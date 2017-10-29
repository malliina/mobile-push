package tests

import java.security.KeyStore

import com.malliina.push.TLSUtils
import com.malliina.push.apns._

import scala.concurrent.Future

class APNS2 extends BaseSuite {
  ignore("a successful notifications returns an apns-id") {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = certContext(creds)
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = false)
      val result = await(client.push(creds.token, request))
      assert(result.right.toOption.isDefined)
    }
  }

  ignore("using a development token with the production API returns BadDeviceToken") {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = certContext(creds)
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = true)
      val result = await(client.push(creds.token, request))
      assert(result.left.toOption.contains(BadDeviceToken))
    }
  }

  ignore("sample code for README") {
    val certKeyStore: KeyStore = ???
    val certPass: String = ???
    val topic = APNSTopic("org.company.MyApp")
    val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").get
    val message = APNSMessage.simple("Hey, sexy!")
    val request = APNSRequest.withTopic(topic, message)
    val client = APNSHttpClient(certKeyStore, certPass, isSandbox = true)
    val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
  }

  def certContext(creds: APNSCred) = {
    val pass = creds.pass
    TLSUtils.keyStoreFromFile(creds.file, pass, "PKCS12")
      .map(ks => TLSUtils.buildSSLContext(ks, pass))
      .get
  }
}
