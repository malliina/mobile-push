package com.malliina.push.apns

import com.malliina.push.{BaseSuite, TLSUtils}
import com.malliina.push.apns._

import java.time.Instant
import java.time.temporal.ChronoUnit

/** To test, obtain a token from a real app, then run a test case manually.
  */
class APNS2 extends BaseSuite {
  test("a successful notifications returns an apns-id".ignore) {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = certContext(creds)
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = false)
      val result = await(client.push(creds.token, request))
      assert(result.isRight)
    }
  }

  http.test("token-authenticated simple notification".ignore) { httpClient =>
    APNSHttpConf.loadOpt.foreach { creds =>
      APNSTokenConf.default.toOption.foreach { conf =>
        val client = APNSTokenClient(conf, httpClient, isSandbox = false)
        val message = APNSMessage.simple("this is a token test")
        val request = APNSRequest.withTopic(creds.topic, message)
        val result = await(client.push(creds.token, request))
        assert(result.isRight)
      }
    }
  }

  http.test("token-authenticated advanced notification".ignore) { httpClient =>
    APNSHttpConf.loadOpt.foreach { creds =>
      APNSTokenConf.default.toOption.foreach { conf =>
        val client = APNSTokenClient(conf, httpClient, isSandbox = false)
        val payload = APSPayload.full(AlertPayload("The Body", title = Option("Attention")))
        val message = APNSMessage(payload)
        val request = APNSRequest.withTopic(creds.topic, message)
        val result = await(client.push(creds.token, request))
        assert(result.isRight)
      }
    }
  }

  test("provider token refresh".ignore) {
    val conf = APNSTokenConf.default.toOption.get
    val client = new APNSTokenPreparer(conf)
    val now = Instant.now()
    val first = client.validToken(now)
    val second = client.validToken(now)
    assert(first == second)
    val third = client.validToken(now.plus(30, ChronoUnit.MINUTES))
    assert(first == third)
    val fourth = client.validToken(now.plus(50, ChronoUnit.MINUTES))
    assert(first != fourth)
  }

  test("using a development token with the production API returns BadDeviceToken".ignore) {
    APNSHttpConf.loadOpt.foreach { creds =>
      val message = APNSMessage.simple("this is a test")
      val request = APNSRequest.withTopic(creds.topic, message)
      val sslContext = certContext(creds)
      val client = APNSHttpClient(sslContext.getSocketFactory, isSandbox = true)
      val result = await(client.push(creds.token, request))
      assert(result.left.toOption.contains(BadDeviceToken))
    }
  }

  def certContext(creds: APNSCred) = {
    val pass = creds.pass
    TLSUtils
      .keyStoreFromFile(creds.file, pass, "PKCS12")
      .map(ks => TLSUtils.buildSSLContext(ks, pass))
      .get
  }
}
