package tests

import java.nio.file.{Path, Paths}

import com.malliina.file.{FileUtilities, StorageFile}
import com.malliina.push.TLSUtils
import com.malliina.push.apns._
import com.malliina.security.KeyStores
import com.malliina.util.BaseConfigReader
import com.notnoop.apns.internal.Utilities

class APNSTests extends BaseSuite {
  implicit val ec = concurrent.ExecutionContext.Implicits.global
//  val rawDeviceID = "9f3c2f830256954ada78bf56894fa7586307f0eedb7763117c84e0c1eee8347a"
  val rawDeviceID = "e0d82212038b938c51dde9f49577ff1f70442fcfe93ec1ff26a2948e36821934"

  ignore("certificate is valid") {
    val creds = APNSCreds.load
    KeyStores.validateKeyStore(creds.file, creds.pass, "PKCS12")
  }

  ignore("universal HTTP2 certificate is valid") {
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

  ignore("send notification with body, if enabled") {
    val creds = APNSHttpConf.load
    val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
    val client = new APNSClient(ks, creds.pass, isSandbox = true)
    val payload = AlertPayload(
      "this is a body",
      title = Some("hey"),
      actionLocKey = Some("POMP"),
      locKey = Some("MSG_FORMAT"),
      locArgs = Some(Seq("Emilia", "Jaana")))
    val message = APNSMessage(APSPayload(Some(Right(payload)), sound = Some("default")))
    val fut = client.push(creds.token, message)
    await(fut)
  }

  ignore("send pimp notification") {
    val creds = APNSHttpConf.load
    val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
    val client = new APNSClient(ks, creds.pass, isSandbox = true)
    val message = APNSMessage.badged("I <3 U!", 3)
    val fut = client.push(creds.token, message)
    await(fut)
  }

  ignore("send background notification, if enabled") {
    val creds = APNSHttpConf.load
    val ks = TLSUtils.keyStoreFromFile(creds.file, creds.pass, "PKCS12").get
    val client = new APNSClient(ks, creds.pass, isSandbox = true)
    val message = APNSMessage.background(badge = 16)
    await(client.push(creds.token, message))
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
