package tests

import java.io.FileInputStream
import java.nio.file.Paths
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
      assert(result.isRight)
    }
  }

  ignore("token-authenticated notification") {
    APNSHttpConf.loadOpt.foreach { creds =>
      APNSConfLoader.default.loadOpt.foreach { conf =>
        val client = APNSTokenClient(conf, isSandbox = false)
        val message = APNSMessage.simple("this is a token test")
        val request = APNSRequest.withTopic(creds.topic, message)
        val result = await(client.push(creds.token, request))
        assert(result.isRight)
      }
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

  ignore("token sample code README") {
    val conf = APNSTokenConf(
      Paths.get("path/to/downloaded-priv-key.p8"),
      KeyId("key_id_here"),
      TeamId("team_id_here")
    )
    val client = APNSTokenClient(conf, isSandbox = true)
    val topic = APNSTopic("org.company.MyApp")
    val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").get
    val message = APNSMessage.simple("Hey, sexy token!")
    val request = APNSRequest.withTopic(topic, message)
    val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
  }

  ignore("cert sample code for README") {
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
    TLSUtils.keyStoreFromResource(new FileInputStream(creds.file.toFile), pass, "PKCS12")
      .map(ks => TLSUtils.buildSSLContext(ks, pass))
      .get
  }
}
