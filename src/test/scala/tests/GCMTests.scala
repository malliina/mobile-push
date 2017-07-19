package tests

import com.malliina.push.gcm.{GCMClient, GCMMessage, GCMToken, MappedGCMResponse}

import scala.concurrent.Future

class GCMTests extends BaseSuite {
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

    val client = new GCMClient(gcmApiKey)
    val message = GCMMessage(Map("title" -> "hey you", "message" -> "late night sexy åäö", "key" -> "value", "a" -> "b"))
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(pushIDs, message)
    val rs = await(response)
    assert(rs.forall(r => r.response.failure === 0))
    rs.foreach(println)
  }
}
