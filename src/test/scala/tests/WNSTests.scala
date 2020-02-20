package tests

import com.malliina.http.OkClient
import com.malliina.push.wns._
import com.malliina.push.{ConfHelper, PushUtils}
import com.malliina.values.ErrorMessage

class WNSTests extends BaseSuite {
  lazy val maybeCreds = WNSConfReader.loadOpt

  test("wns regex accepts valid input") {
    val validInput =
      "https://db3.notify.windows.com/?token=AgUAAADCQmTg7OMlCg%2fK0K8rBPcBqHuy%2b1rTSNPMuIzF6BtvpRdT7DM4j%2fs%2bNNm8z5l1QKZMtyjByKW5uXqb9V7hIAeA3i8FoKR%2f49ZnGgyUkAhzix%2fuSuasL3jalk7562F4Bpw%3d"
    assert(WNSToken.isValid(validInput))
  }

  test("wns regex discards invalid input") {
    val invalidInput = "https://www.google.com/hey"
    assert(!WNSToken.isValid(invalidInput))
  }

  ignore("can read credentials") {
    assert(maybeCreds.isDefined)
  }

  ignore("can fetch token") {
    val token = maybeCreds map { creds =>
      val client = new WNSClient(creds)
      await(client.fetchAccessToken(OkClient.default))
    }
    assert(token.forall(_.access_token.nonEmpty))
  }

  ignore("send") {
    val token = WNSToken
      .build(
        "https://db5.notify.windows.com/?token=AwYAAABq7aWoYUJUr%2fM%2bRcWZacCYWN3cutxpadhmsejNg7aOJQselRS9AEE3ubPwZlLBcjYmYNzHFezNQoPyrViQRtPlvpxMXNREJHPVCmBDMG7wZWhRb1sxDCatCYsiafv0a6I%3d"
      )
      .get
    val payload = ToastElement.text("Hello, world!")
    val message = WNSMessage(payload)
    maybeCreds foreach { creds =>
      val client = new WNSClient(creds)
      val response = await(client.push(token, message))
      assert(response.isSuccess)
    }
  }
}

object WNSConfReader extends ConfHelper[WNSCredentials] {
  val file = PushUtils.userHome.resolve("keys/wns.key")

  def loadOpt = fromFile(file).toOption

  override def parse(
    readKey: String => Either[ErrorMessage, String]
  ): Either[ErrorMessage, WNSCredentials] = for {
    sid <- readKey("sid")
    secret <- readKey("clientSecret")
  } yield WNSCredentials(sid, secret)
}
