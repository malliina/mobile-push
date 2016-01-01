package tests

import java.io.FileInputStream
import java.nio.file.{Path, Paths}
import java.security.KeyStore

import com.malliina.file.{FileUtilities, StorageFile}
import com.malliina.push.apns._
import com.malliina.security.KeyStores
import com.malliina.util.{BaseConfigReader, Util}
import com.notnoop.apns.internal.Utilities
import org.scalatest.FunSuite
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.Try

/**
 * @author Michael
 */
class APNSTests extends FunSuite {
  val rawDeviceID = "9f3c2f830256954ada78bf56894fa7586307f0eedb7763117c84e0c1eee8347a"
  val deviceID: Option[APNSToken] = None
//  val deviceID: Option[APNSToken] = APNSToken.build(rawDeviceID)

  test("certificate is valid") {
    val creds = APNSCreds.load
    KeyStores.validateKeyStore(creds.file, creds.pass, "PKCS12")
  }

  test("token validation") {
    val tokenOpt = APNSToken.build(rawDeviceID)
    assert(tokenOpt.isDefined)
    intercept[RuntimeException](Utilities.decodeHex("deviceToken"))
    val invalidToken = APNSToken.build("deviceToken")
    assert(invalidToken.isEmpty)
  }

  test("send notification with body, if enabled") {
    deviceID.foreach(token => {
      val creds = APNSCreds.load
      val ks = keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
      val client = new APNSClient(ks, creds.pass, isSandbox = true)
      //    val message = APNSMessage.badged("I <3 U!", 3)
      val payload = AlertPayload(
        "this is a body",
        title = Some("hey"),
        actionLocKey = Some("POMP"),
        locKey = Some("MSG_FORMAT"),
        locArgs = Some(Seq("Emilia", "Jaana")))
      val advancedMessage = APNSMessage(APSPayload(Some(Right(payload)), sound = Some("default")))
      val fut = client.push(token, advancedMessage)
      Await.result(fut, 5.seconds)
    })
  }

  test("send background notification, if enabled") {
    deviceID.foreach(token => {
      val creds = APNSCreds.load
      val ks = keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
      val client = new APNSClient(ks, creds.pass, isSandbox = true)
      val message = APNSMessage.background(badge = 15)
      println(Json.prettyPrint(Json.toJson(message)))
      Await.result(client.push(token, message), 5.seconds)
    })
  }

  def keyStoreFromFile(file: Path, pass: String, storeType: String = "JKS"): Try[KeyStore] = Try {
    val ks = KeyStore.getInstance(storeType)
    Util.using(new FileInputStream(file.toFile))(keyStream => {
      ks.load(keyStream, pass.toCharArray)
      ks
    })
  }
}

case class APNSCred(file: Path, pass: String)

object APNSCreds extends BaseConfigReader[APNSCred] {
  override def filePath: Option[Path] = Option(FileUtilities.userHome / "keys" / "aps.conf")

  override def loadOpt: Option[APNSCred] = fromUserHomeOpt

  override def fromMapOpt(map: Map[String, String]): Option[APNSCred] = for {
    file <- map get "aps_file"
    pass <- map get "aps_pass"
  } yield APNSCred(Paths get file, pass)
}
