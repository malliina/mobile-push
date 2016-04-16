package tests

import com.malliina.push.gcm.{GCMClient, GCMMessage, GCMToken, MappedGCMResponse}
import org.scalatest.FunSuite

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class GCMTests extends FunSuite {
  val rawToken = "APA91bHyeY6NdQar-XXoC47PuWB0eCZErLB-xBNSlhrXQ-u_ElWM7ZFaocsoCeWBx_Or5vmj357BNTdr6atRNwAfFQ4od458OqwfJV3SSPnYa1CIN1j0EVplN8QeEjx3n6-WV6obKN60CDn0-RL3gAsILC_4ec0gAQ"
  //  val emuID = Some(rawToken)
  val emuID: Option[GCMToken] = None

  test("token validation") {
    val tokenOpt = GCMToken.build(rawToken)
    assert(tokenOpt.isDefined)
  }

  test("send message, if enabled") {
    emuID.foreach(token => {
      //    val gcmApiKey: String = "AIzaSyCCDniLRhlHAfnXIJnsVn-You2QQKLfrM8"
      val gcmApiKey: String = "AIzaSyBLwdU7XGCdEPlwkGXW7V2eMRRFieNGYmA"
      val pushIDs = Seq(token)

      val client = new GCMClient(gcmApiKey)
      val message = GCMMessage(Map("title" -> "hey you", "message" -> "late night sexy åäö", "key" -> "value"))
      val response: Future[Seq[MappedGCMResponse]] = client.pushAll(pushIDs, message)
      val rs = Await.result(response, 5.seconds)
      assert(rs.forall(r => r.response.failure === 0))
      rs.foreach(println)
    })
  }
}
