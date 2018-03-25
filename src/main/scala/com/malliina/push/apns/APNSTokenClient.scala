package com.malliina.push.apns

import java.nio.file.Path
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.{Base64, Date}

import com.malliina.http.OkClient
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import okhttp3.Request

import scala.io.Source

object APNSTokenClient {
  def default = apply(APNSTokenConf.default.right.get, isSandbox = false)

  def apply(conf: APNSTokenConf, isSandbox: Boolean): APNSTokenClient =
    new APNSTokenClient(conf, isSandbox)
}

/** https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/CommunicatingwithAPNs.html
  */
class APNSTokenClient(conf: APNSTokenConf, isSandbox: Boolean)
  extends APNSHttpClient(OkClient.default, isSandbox) {

  val keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder.decode(readKey(conf.privateKey)))
  val keyFactory = KeyFactory.getInstance("EC")
  val key = keyFactory.generatePrivate(keySpec).asInstanceOf[ECPrivateKey]
  val signer = new ECDSASigner(key)
  val jwtHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(conf.keyId.id).build()

  override def installHeaders(request: Request.Builder): Request.Builder =
    request.header("authorization", newHeader())

  private def newHeader(): String = {
    val issuedAt = Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond))
    val claimsSet = new JWTClaimsSet.Builder()
      .issuer(conf.teamId.team)
      .issueTime(issuedAt)
      .build()
    val signable = new SignedJWT(jwtHeader, claimsSet)
    signable.sign(signer)
    headerValue(signable)
  }

  private def headerValue(signed: SignedJWT) = {
    val serialized = signed.serialize()
    s"bearer $serialized"
  }

  // drops 'begin private key...', 'end private key...' boiler
  private def readKey(file: Path) = Source.fromFile(file.toFile).getLines().toList.drop(1).init.mkString
}
