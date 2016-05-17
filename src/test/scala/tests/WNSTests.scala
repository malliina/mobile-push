package tests

import java.nio.file.Path

import com.malliina.file.{FileUtilities, StorageFile}
import com.malliina.push.wns.{WNSClient, WNSCredentials, WNSToken}
import com.malliina.util.BaseConfigReader

class WNSTests extends BaseSuite {
  val maybeCreds = WNSConfReader.loadOpt

  test("wns regex accepts valid input") {
    val validInput = "https://db3.notify.windows.com/?token=AgUAAADCQmTg7OMlCg%2fK0K8rBPcBqHuy%2b1rTSNPMuIzF6BtvpRdT7DM4j%2fs%2bNNm8z5l1QKZMtyjByKW5uXqb9V7hIAeA3i8FoKR%2f49ZnGgyUkAhzix%2fuSuasL3jalk7562F4Bpw%3d"
    assert(WNSToken.isValid(validInput))
  }

  test("wns regex discards invalid input") {
    val invalidInput = "https://www.google.com/hey"
    assert(!WNSToken.isValid(invalidInput))
  }

  test("can read credentials") {
    assert(maybeCreds.isDefined)
  }

  test("can fetch token") {
    val token = maybeCreds map { creds =>
      val client = new WNSClient(creds)
      await(client.fetchAccessToken())
    }
    assert(token.forall(_.access_token.nonEmpty))
  }
}

object WNSConfReader extends BaseConfigReader[WNSCredentials] {
  override def filePath: Option[Path] = Option(FileUtilities.userHome / "keys" / "wns.key")

  override def fromMapOpt(map: Map[String, String]): Option[WNSCredentials] = for {
    sid <- map get "sid"
    secret <- map get "clientSecret"
  } yield WNSCredentials(sid, secret)
}
