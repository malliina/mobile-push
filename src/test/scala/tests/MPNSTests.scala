package tests

import com.malliina.push.mpns.{MPNSClient, MPNSToken, TileData, ToastMessage}

class MPNSTests extends BaseSuite {
  val devices = Seq(
    "http://e.notify.live.net/u/1/db5/H2QAAAAPILWtGez2kX5YLZ2VkypFvh21zCkjjotYQkVVdQJh73mC2YqAB0MwX3muyPNXLs9CEZeu7bWrmmJMFacm5vBVdoEf3YG3Q4K0x47QigL3GsMgpRaU-w_qu2W0fweWCuY/d2luZG93c3Bob25lZGVmYXVsdA/MJDNhFxKA0qwq01Zwvp9Qg/LftbTayvYfqUue0Q7AzWLo4t4d0"
  ).map(MPNSToken.apply)

  val rawToken = "http://s.notify.live.net/u/1/db3/H2QAAAAvWyd0kMrYHTG0P7Df7j_Saq2DLEkpDg-Ef4Yd9ia9p2QArOuttcjvzSo7byvXzfuwYtPYJxh5RuFpHNFfYGwmrm6ufNWgZ6-s7CEVBz99UBi-zT5yU_N1vbtltIls4HA/d2luZG93c3Bob25lZGVmYXVsdA/vnjifiZVeEyAAmiZ81DJ8w/b3D8tgehywO4GG7X8eQHI7abkJ0"
  val invalidToken = "http://.com"

  test("token validation") {
    val tokenOpt = MPNSToken.build(rawToken)
    assert(tokenOpt.isDefined)

    val failure = MPNSToken.build(invalidToken)
    assert(failure.isEmpty)
  }

  test("validate token") {
    assert(MPNSToken.isValid(rawToken))
    assert(!MPNSClient.isTokenValid(invalidToken))
  }

  ignore("can send toast") {
    val client = new MPNSClient
    val message = ToastMessage("hey", "you all åäö", "/MainPage.xaml", silent = false)
    val f = client.pushAll(devices, message)
    val rs = await(f)
    assert(rs.forall(r => r.code === 200))
  }

  ignore("can send tile") {
    val client = new MPNSClient
    val message = TileData("img.jpg", 1, "Title here - Good Night", "no.jpg", "Back title here", "Back content here")
    val f = client.pushAll(devices, message)
    val rs = await(f)
    assert(rs.forall(r => r.code === 200))
  }
}
