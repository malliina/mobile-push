package tests

import com.mle.push.gcm.{GCMClient, GCMMessage, GCMResponse}
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
    val sonyID2 = "APA91bFAJ2RF4KaMLM474dJqnMaj5tXgEDAgPciTG629Mz2CNrlM9u2pUCUIarT-CUvGuPLwiRV-PHHacjNpbTLslUx36vfhTqxpC06MuJ_1vEaDyy2kCmE34Q1qd_yQQV-nov5EVEAFVET5XZVjY_Rh-Hnza6w6kQ"
    val sonyID = "APA91bHPl4qNXNd4n2SkL-T8wTK18IVg4Z4dgEFQopEc_zlDV3yrPqAlgYCnmV2zldleO9JGlv_Y_-_uwnAw1tayFtEX2SCs7wTWPC4ja_y0oO2u_RHiQHc8pRdmPNbpEZG5_v7JMANtdGWNrKK7hiLRgNYz5_WFFg"
    val emuID = "APA91bHa-eNoPlMQnNMfm08wFI9_m_pkD8dN05Ftp5JeOEdu-objuWKlkGLqRb2cTOcbhLdYe5rH3c2lM3-s_oV9l49o4YNLeASzyQpFkFqDLyBscGV52yXZLgx5reZd0Eg7OmkAyOLFM0fORzZfkApTrzEHhIjbsw"
    val emuID2 = "APA91bFBtd-rQgjPsUDSPx2wDj45_GRW4LF8y7lLUTEd6jX7--2avKNL7wahI7hJxUKCcZ7tnyEMsNP3J2r_SP8RuY5mMeLuvcOF_-C_RE1wIT6WAliRc99YS_krPO0yxC47Ju8XXVuycuIwVlMiWhxOnbPZzJ3I4Q"
    val emuID3 = "APA91bEiH7cOSxATyeMu5OcP0MSxBe3iKxhc4gTK360f5z86_3GifThBvsdoA-Ugllg5ZkrxZSj2PHZyT4QlraKf6L0WkxH2HL5FdogJDbE8AKWR-5HuJaGc4dkudIAwO5y6axqO-ukppAp9WbW0R_yAF2TAh53iRQ"
    val pushIDs = Seq(emuID2, emuID3)

    val client = new GCMClient(gcmApiKey)
    val message = GCMMessage(Map("title" -> "hey you", "message" -> "late night sexy!", "key" -> "value"))
    val response: Future[Seq[GCMResponse]] = client.pushAllParsed(pushIDs, message)
    val rs = Await.result(response, 5.seconds)
    assert(rs.forall(r => r.failure === 0))
    rs.foreach(println)
  }
}
