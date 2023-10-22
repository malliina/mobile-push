package com.malliina.push.apns

import com.malliina.push.apns._
import com.malliina.push.{BaseSuite, ConfHelper, PushUtils}
import com.malliina.values.ErrorMessage

import java.nio.file.{Path, Paths}
import scala.concurrent.ExecutionContext

class APNSTests extends BaseSuite {
  //  val rawDeviceID = "9f3c2f830256954ada78bf56894fa7586307f0eedb7763117c84e0c1eee8347a"
  val rawDeviceID = "e0d82212038b938c51dde9f49577ff1f70442fcfe93ec1ff26a2948e36821934"
  implicit val ec: ExecutionContext = munitExecutionContext

  test("token validation") {
    val tokenOpt = APNSToken.build(rawDeviceID)
    assert(tokenOpt.isRight)
    val invalidDeviceToken = "deviceToken"
    val invalidToken = APNSToken.build(invalidDeviceToken)
    assert(invalidToken.isLeft)
  }

  //  ignore("certificate is valid") {
  //    val creds = APNSCreds.load
  //    KeyStores.validateKeyStore(creds.file, creds.pass, "PKCS12")
  //  }
  //
  //  ignore("universal HTTP2 certificate is valid") {
  //    val creds = APNSHttpConf.load
  //    KeyStores.validateKeyStore(creds.file, creds.pass, "PKCS12")
  //  }
}

case class APNSCred(file: Path, pass: String, topic: APNSTopic, token: APNSToken)

object APNSCreds extends FileAPNSConf(PushUtils.userHome.resolve("keys/apns/aps.conf"))

object APNSHttpConf extends FileAPNSConf(PushUtils.userHome.resolve("keys/apns/apnshttp.conf"))

class FileAPNSConf(file: Path) extends ConfHelper[APNSCred] {

  def load: APNSCred = load(file)

  def loadOpt = fromFile(file).toOption

  override def parse(
    readKey: String => Either[ErrorMessage, String]
  ): Either[ErrorMessage, APNSCred] =
    for {
      file <- readKey("aps_file")
      pass <- readKey("aps_pass")
      topic <- readKey("topic")
      hexToken <- readKey("token")
      token <- APNSToken.build(hexToken)
    } yield APNSCred(Paths get file, pass, APNSTopic(topic), token)
}

case class APNSInfo(topic: APNSTopic, token: APNSToken)

object APNSLoader extends APNSLoader(PushUtils.userHome.resolve(".apple/apnsToken.conf"))

class APNSLoader(file: Path) extends ConfHelper[APNSInfo] {

  def load: APNSInfo = load(file)

  override def parse(
    readKey: String => Either[ErrorMessage, String]
  ): Either[ErrorMessage, APNSInfo] =
    for {
      topic <- readKey("topic")
      hexToken <- readKey("token")
      token <- APNSToken.build(hexToken)
    } yield APNSInfo(APNSTopic(topic), token)
}
