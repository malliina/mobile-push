package com.malliina.push.apns

import com.malliina.push.{BaseSuite, PushUtils, TLSUtils}
import com.malliina.push.apns._
import io.circe.{Codec, Json}
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps

import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.net.ssl.SSLContext

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
    APNSTokenConf
      .fromFile(PushUtils.userHome.resolve(".boat/apns.conf"))
      .foreach { conf =>
        val token = APNSToken(
          "changeme"
        )
        val client = APNSTokenClient(conf, httpClient, isSandbox = false)
        val message = APNSMessage.simple("this is a token test")
        val request = APNSRequest.withTopic(APNSTopic("com.malliina.BoatTracker"), message)
        val result = await(client.push(token, request))
        assert(result.isRight)
      }
  }

  case class LiveActivityState(
    message: String,
    distance: Double,
    duration: Double,
    address: Option[String]
  )

  object LiveActivityState {
    implicit val json: Codec[LiveActivityState] = deriveCodec[LiveActivityState]
  }

  http.test("Start live activity".ignore) { httpClient =>
    val token = APNSToken(
      "changeme"
    )
    APNSTokenConf
      .fromFile(PushUtils.userHome.resolve(".boat/apns.conf"))
      .foreach { conf =>
        val client = APNSTokenClient(conf, httpClient, isSandbox = true)
        val payload = APSPayload.startLiveActivity(
          Instant.now(),
          "BoatWidgetAttributes",
          Json.obj("boatName" -> "boat 1".asJson, "trackName" -> "track1".asJson),
          LiveActivityState("Boating", 123, 111, Option("Road 2")),
          Right(AlertPayload("moving", title = Option("on the move"))),
          None
        )
        val message = APNSMessage(payload, Map.empty)
        val req = client.push(
          token,
          APNSRequest.liveActivity(APNSTopic("com.malliina.BoatTracker"), message)
        )
        val res = await(req)
        println(s"Got $res")
      }
  }

  http.test("Update live activity".ignore) { httpClient =>
    val token = APNSToken("changeme")
    APNSTokenConf
      .fromFile(PushUtils.userHome.resolve(".boat/apns.conf"))
      .foreach { conf =>
        val client = APNSTokenClient(conf, httpClient, isSandbox = true)
        val payload = APSPayload.updateLiveActivity(
          Instant.now(),
          LiveActivityState("Boating more 2", 234, 444, None),
          None,
          None,
          None
        )
        val message = APNSMessage(payload, Map.empty)
        val req = client.push(
          token,
          APNSRequest.liveActivity(APNSTopic("com.malliina.BoatTracker"), message)
        )
        val res = await(req)
        println(s"Got $res")
      }
  }

  http.test("token-authenticated advanced notification".ignore) { httpClient =>
    val creds = APNSLoader.load
    APNSTokenConf.default.toOption.foreach { conf =>
      val client = APNSTokenClient(conf, httpClient, isSandbox = false)
      val payload = APSPayload.full(
        AlertPayload("The Body", title = Option("Car-Map")),
        sound = Option("default")
      )
      val message = APNSMessage(payload)
      val request = APNSRequest.withTopic(creds.topic, message)
      val result = await(client.push(creds.token, request))
      println(result)
      assert(result.isRight)
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

  def certContext(creds: APNSCred): SSLContext = {
    val pass = creds.pass
    TLSUtils
      .keyStoreFromFile(creds.file, pass, "PKCS12")
      .map(ks => TLSUtils.buildSSLContext(ks, pass))
      .get
  }
}
