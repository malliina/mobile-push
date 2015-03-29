package tests

import com.mle.push.gcm.{MappedGCMResponse, GCMClient, GCMMessage, GCMResponse}
import org.scalatest.FunSuite

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
 * @author Michael
 */
class GCMTests extends FunSuite {
  test("send message") {
    //    val gcmApiKey: String = "AIzaSyCCDniLRhlHAfnXIJnsVn-You2QQKLfrM8"
    val gcmApiKey: String = "AIzaSyBLwdU7XGCdEPlwkGXW7V2eMRRFieNGYmA"
    val emuID4 = "APA91bHyeY6NdQar-XXoC47PuWB0eCZErLB-xBNSlhrXQ-u_ElWM7ZFaocsoCeWBx_Or5vmj357BNTdr6atRNwAfFQ4od458OqwfJV3SSPnYa1CIN1j0EVplN8QeEjx3n6-WV6obKN60CDn0-RL3gAsILC_4ec0gAQ"
    val pushIDs = Seq(emuID4)

    val client = new GCMClient(gcmApiKey)
    val message = GCMMessage(Map("title" -> "hey you", "message" -> "late night sexy åäö", "key" -> "value"))
    val response: Future[Seq[MappedGCMResponse]] = client.pushAll(pushIDs, message)
    val rs = Await.result(response, 5.seconds)
    assert(rs.forall(r => r.response.failure === 0))
    rs.foreach(println)
  }
}
