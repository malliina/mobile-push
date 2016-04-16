package tests

import java.nio.file.{Path, Paths}

import com.malliina.file.{FileUtilities, StorageFile}
import com.malliina.push.TLSUtils
import com.malliina.push.apns._
import com.malliina.security.KeyStores
import com.malliina.util.BaseConfigReader
import com.notnoop.apns.internal.Utilities
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class APNSTests extends FunSuite {

  val enabled: Boolean = true
  val rawDeviceID = "6c9969eee832f6ed2a11d04d6daa404db13cc3d97f7298f0c042616fc2a5cc34"

  test("certificate is valid") {
    val creds = APNSCreds.load
    KeyStores.validateKeyStore(creds.file, creds.pass, "PKCS12")
  }

  test("universal HTTP2 certificate is valid") {
    val creds = APNSHttpConf.load
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
    if (enabled) {
      val creds = APNSHttpConf.load
      val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
      val client = new APNSClient(ks, creds.pass, isSandbox = false)
      //    val message = APNSMessage.badged("I <3 U!", 3)
      val payload = AlertPayload(
        "this is a body",
        title = Some("hey"),
        actionLocKey = Some("POMP"),
        locKey = Some("MSG_FORMAT"),
        locArgs = Some(Seq("Emilia", "Jaana")))
      val message = APNSMessage(APSPayload(Some(Right(payload)), sound = Some("default")))
      val fut = client.push(creds.token, message)
      Await.result(fut, 5.seconds)
    }
  }

  test("send pimp notification") {
    if (enabled) {
      val creds = APNSHttpConf.load
      val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
      val client = new APNSClient(ks, creds.pass, isSandbox = false)
      val message = APNSMessage.badged("I <3 U!", 3)
      val fut = client.push(creds.token, message)
      Await.result(fut, 5.seconds)
    }
  }

  test("send background notification, if enabled") {
    if (enabled) {
      val creds = APNSHttpConf.load
      val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
      val client = new APNSClient(ks, creds.pass, isSandbox = true)
      val message = APNSMessage.background(badge = 16)
      Await.result(client.push(creds.token, message), 5.seconds)
    }
  }
}

case class APNSCred(file: Path, pass: String, topic: APNSTopic, token: APNSToken)

object APNSCreds extends FileAPNSConf(FileUtilities.userHome / "keys" / "apns" / "aps.conf")

object APNSHttpConf extends FileAPNSConf(FileUtilities.userHome / "keys" / "apns" / "apnshttp.conf")

class FileAPNSConf(file: Path) extends BaseConfigReader[APNSCred] {
  override def filePath: Option[Path] = Option(file)

  override def loadOpt: Option[APNSCred] = fromUserHomeOpt

  override def fromMapOpt(map: Map[String, String]): Option[APNSCred] = for {
    file <- map get "aps_file"
    pass <- map get "aps_pass"
    topic <- map get "topic"
    hexToken <- map get "token"
    token <- APNSToken.build(hexToken)
  } yield APNSCred(Paths get file, pass, APNSTopic(topic), token)
}
