package tests

import com.mle.push.android.AndroidMessage
import com.mle.push.gcm.GCMClient
import com.ning.http.client.Response
import org.scalatest.FunSuite

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
 * @author Michael
 */
class GCMTests extends FunSuite {
  test("send message") {
    val gcmApiKey: String = "AIzaSyCCDniLRhlHAfnXIJnsVn-You2QQKLfrM8"
    val sonyID2 = "APA91bFAJ2RF4KaMLM474dJqnMaj5tXgEDAgPciTG629Mz2CNrlM9u2pUCUIarT-CUvGuPLwiRV-PHHacjNpbTLslUx36vfhTqxpC06MuJ_1vEaDyy2kCmE34Q1qd_yQQV-nov5EVEAFVET5XZVjY_Rh-Hnza6w6kQ"
    val sonyID = "APA91bHPl4qNXNd4n2SkL-T8wTK18IVg4Z4dgEFQopEc_zlDV3yrPqAlgYCnmV2zldleO9JGlv_Y_-_uwnAw1tayFtEX2SCs7wTWPC4ja_y0oO2u_RHiQHc8pRdmPNbpEZG5_v7JMANtdGWNrKK7hiLRgNYz5_WFFg"
    val emuID = "APA91bHa-eNoPlMQnNMfm08wFI9_m_pkD8dN05Ftp5JeOEdu-objuWKlkGLqRb2cTOcbhLdYe5rH3c2lM3-s_oV9l49o4YNLeASzyQpFkFqDLyBscGV52yXZLgx5reZd0Eg7OmkAyOLFM0fORzZfkApTrzEHhIjbsw"
    val client = new GCMClient(gcmApiKey)
    val message = AndroidMessage(Map("title" -> "hey", "message" -> "msg", "key" -> "value"), expiresAfter = 20.seconds)
    val response: Future[Response] = client.push(sonyID2, message)
    val r = Await.result(response, 5.seconds)
    println(r.getStatusText)
    println(r.getResponseBody)
  }
}
