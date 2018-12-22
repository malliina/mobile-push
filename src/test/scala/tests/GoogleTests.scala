package tests

import com.malliina.push.fcm.FCMLegacyClient
import com.malliina.push.gcm._

import scala.concurrent.Future

class GoogleTests extends BaseSuite {
  val rawToken = "APA91bHyeY6NdQar-XXoC47PuWB0eCZErLB-xBNSlhrXQ-u_ElWM7ZFaocsoCeWBx_Or5vmj357BNTdr6atRNwAfFQ4od458OqwfJV3SSPnYa1CIN1j0EVplN8QeEjx3n6-WV6obKN60CDn0-RL3gAsILC_4ec0gAQ"
  //  val token = GCMToken(rawToken)
  // emulator token
  val token = GCMToken("APA91bGrvnDeCTk7neV4yjN5CPtbMF7XuSpsxgA4B4K9knDsBgzPn8PaVuz1o_50ot2-ydNZJ8smTLehT6hehmbXtVi-s4kBJkkavXohgWgrbHo6vVtbPP4")
  //  val emuID: Option[GCMToken] = None

  test("token validation") {
    val tokenOpt = GCMToken.build(rawToken)
    assert(tokenOpt.isDefined)
  }

  ignore("send message, if enabled") {
    val gcmApiKey: String = "AIzaSyCCDniLRhlHAfnXIJnsVn-You2QQKLfrM8"
    //    val gcmApiKey: String = "AIzaSyBLwdU7XGCdEPlwkGXW7V2eMRRFieNGYmA"
    val pushIDs = Seq(token)

    val client = GCMClient(gcmApiKey)
    val message = GCMMessage(Map("title" -> "hey you", "message" -> "late åäö", "key" -> "value", "a" -> "b"))
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(pushIDs, message)
    val rs = await(response)
    assert(rs.forall(r => r.response.failure === 0))
    rs.foreach(println)
  }

  ignore("FCM") {
    val tokens = Seq(
      GCMToken("euPV3FwOfAw:APA91bFGBF4aucsQYmZF4OdGp6WDUXfAbxzi2m7UQ2nustPqyA6ASgvXXjUwZ2x8z-s-3fZo6xtVyrWoGRInaL2dXp1pHQYMt2aQ1CnP4EAX3Z8WiojWNJnBORuuTbOiXFJ1A7549AT-")
    )
    val gcmApiKey: String = "AIzaSyBTtiOW0u5J11LzRMSqQlGrQYl4l-CgG-I"
    val client = FCMLegacyClient(gcmApiKey)
    val message = GCMMessage(Map("title" -> "hey you", "message" -> "late åäö", "key" -> "value", "a" -> "b"))
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(tokens, message)
    val rs = await(response)
    println(rs)
    assert(rs.forall(r => r.response.failure === 0))
    rs.foreach(println)
  }
}
