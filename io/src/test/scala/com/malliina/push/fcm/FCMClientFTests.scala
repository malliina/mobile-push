package com.malliina.push.fcm

import com.malliina.http.io.{HttpClientF, HttpClientIO}
import com.malliina.push.gcm.{GCMMessage, GCMToken}
import tests.BaseSuite

class FCMClientFTests extends BaseSuite {
  val httpIo = FunFixture[HttpClientIO](
    opts => HttpClientIO(),
    teardown = _.close()
  )
  httpIo.test("FCM send".ignore) { httpClient =>
    val boatToken = GCMToken("changeme")
    val fcmApiKey: String = "changeme"
    val client = FCMClientF(fcmApiKey, httpClient)
    val message = GCMMessage(
      Map("title" -> "hey from fcm", "message" -> "late åäö", "key" -> "value", "a" -> "b")
    )
    val res = client.push(boatToken, message).unsafeRunSync()
    println(res)
  }
}
