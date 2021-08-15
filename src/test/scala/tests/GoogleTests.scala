package tests

import com.malliina.push.ResponseException
import com.malliina.push.fcm.FCMLegacyClient
import com.malliina.push.gcm._

import scala.concurrent.{ExecutionContext, Future}

class GoogleTests extends BaseSuite {
  implicit val ec: ExecutionContext = munitExecutionContext
  val rawToken =
    "APA91bHyeY6NdQar-XXoC47PuWB0eCZErLB-xBNSlhrXQ-u_ElWM7ZFaocsoCeWBx_Or5vmj357BNTdr6atRNwAfFQ4od458OqwfJV3SSPnYa1CIN1j0EVplN8QeEjx3n6-WV6obKN60CDn0-RL3gAsILC_4ec0gAQ"
  // emulator token
  val token = GCMToken("changeme")

  test("token validation") {
    val tokenOpt = GCMToken.build(rawToken)
    assert(tokenOpt.isRight)
  }

  http.test("send message, if enabled".ignore) { httpClient =>
    val boatToken = GCMToken("changeme")
    val fcmApiKey: String = "changeme"
    val pushIDs = Seq(boatToken)

    val client = FCMLegacyClient(fcmApiKey, httpClient, munitExecutionContext)
    val message =
      GCMMessage(Map("title" -> "hey you", "message" -> "late åäö", "key" -> "value", "a" -> "b"))
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(pushIDs, message)
    val rs = await(response)
    assert(rs.forall(r => r.response.failure == 0))
    rs.foreach(println)
  }

  http.test("FCM".ignore) { httpClient =>
    val tokens = Seq(
      // Emulator
      GCMToken(
        "ctCUlTdaxxI:APA91bH-aBVc0qoUwLCRNV0VJ27Nng6CYhy7jiILHaEqQMGUQWxLocsWawWSIUq0gw6xlXttuLsddwbnP5Tjjw7-DfJNuV1UmDJgmQHajh8BHnSWFCFhkXWhV4y0D7tB7CL2tQhzVTIV"
      )
    )
    // Check https://console.firebase.google.com/u/0/project/project-id-here/settings/cloudmessaging/?pli=1
    val gcmApiKey: String = "changeme"
    val client = FCMLegacyClient(gcmApiKey, httpClient, munitExecutionContext)
    val message = GCMMessage(
      Map("title" -> "hey you there", "message" -> "late åäö", "key" -> "value", "a" -> "b")
    )
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(tokens, message)
    val rs = await(response)
    println(rs)
    assert(rs.forall(r => r.response.failure == 0))
    rs.foreach(println)
  }
}
