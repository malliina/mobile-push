package tests

import com.mle.push.mpns.{MPNSClient, TileData, ToastMessage}
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 * @author Michael
 */
class MPNSTests extends FunSuite {
//  val devices = Seq(
//    "http://s.notify.live.net/u/1/db3/HmQAAAB_5whAEO6GhJpi2BjBxhKedc4c5A5vCeH50U9hOSbcDS3qNOAIFjMmfyxJ3SSSTZMpc6NdxiehrGtcFB4rl3KU/d2luZG93c3Bob25lZGVmYXVsdA/vnjifiZVeEyAAmiZ81DJ8w/GSq1vU6RyYMIc9ZnJSfs-jpZoAk",
//    "http://s.notify.live.net/u/1/db3/HmQAAAC7ZLGhuu1q4QzaD6MdN_qhULt8BHWE0XzOHs_R73Wr0qQuOqtHfpTuHHbtSpctsi8g3ZWFgu_PE7kGLCuElK44/d2luZG93c3Bob25lZGVmYXVsdA/vnjifiZVeEyAAmiZ81DJ8w/qVX6TH1mul8u93I3qUdD2oA1fjQ"
//  )

  val mpnsToken = "http://s.notify.live.net/u/1/db3/H2QAAAAvWyd0kMrYHTG0P7Df7j_Saq2DLEkpDg-Ef4Yd9ia9p2QArOuttcjvzSo7byvXzfuwYtPYJxh5RuFpHNFfYGwmrm6ufNWgZ6-s7CEVBz99UBi-zT5yU_N1vbtltIls4HA/d2luZG93c3Bob25lZGVmYXVsdA/vnjifiZVeEyAAmiZ81DJ8w/b3D8tgehywO4GG7X8eQHI7abkJ0"
  val devices = Seq(mpnsToken)
  //  val devices = Nil
  val invalidToken = "http://.com"

  test("can send toast") {
    val client = new MPNSClient
    val message = ToastMessage("hey", "you all åäö", "/ServicePage.xaml", silent = false)
    val f = client.pushAll(devices, message)
    val rs = Await.result(f, 5.seconds)
    assert(rs.forall(r => r.getStatusCode === 200))
  }
  test("can send tile") {
    val client = new MPNSClient
    val message = TileData("img.jpg", 1, "Title here - Good Night", "no.jpg", "Back title here", "Back content here")
    val f = client.pushAll(devices, message)
    val rs = Await.result(f, 5.seconds)
    assert(rs.forall(r => r.getStatusCode === 200))
  }
  test("validate token") {
    assert(devices.forall(MPNSClient.isTokenValid))
    assert(!MPNSClient.isTokenValid(invalidToken))
  }
}
