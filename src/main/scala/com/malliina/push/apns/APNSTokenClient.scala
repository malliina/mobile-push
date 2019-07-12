package com.malliina.push.apns

import java.nio.file.Path
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicReference
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

/** <p>For security, APNs requires you to refresh your token regularly. Refresh your token no more than once every
  * 20 minutes and no less than once every 60 minutes. APNs rejects any request whose token contains a timestamp
  * that is more than one hour old. Similarly, APNs reports an error if you recreate your tokens more than once
  * every 20 minutes.</p>
  *
  * <p>On your provider server, set up a recurring task to recreate your token with a current timestamp. Encrypt
  * the token again and attach it to subsequent notification requests.</p>
  *
  * @see https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/CommunicatingwithAPNs.html
  */
class APNSTokenClient(conf: APNSTokenConf, isSandbox: Boolean)
    extends APNSHttpClient(OkClient.default, isSandbox)
    with AutoCloseable {

  val keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder.decode(readKey(conf.privateKey)))
  val keyFactory = KeyFactory.getInstance("EC")
  val key = keyFactory.generatePrivate(keySpec).asInstanceOf[ECPrivateKey]
  val signer = new ECDSASigner(key)
  val jwtHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(conf.keyId.id).build()

  private val providerToken: AtomicReference[SignedJWT] =
    new AtomicReference[SignedJWT](newProviderToken(Instant.now()))

  override def installHeaders(request: Request.Builder): Request.Builder =
    request.header("authorization", headerValue(validToken(Instant.now())))

  def validToken(now: Instant): SignedJWT =
    providerToken.updateAndGet { token =>
      // Regenerates the provider token if it's more than 40 minutes old, as per Apple's guidelines
      val notBefore = Date.from(now.minus(40, ChronoUnit.MINUTES))
      if (token.getJWTClaimsSet.getIssueTime.before(notBefore)) newProviderToken(now) else token
    }

  private def newProviderToken(now: Instant): SignedJWT = {
    val issuedAt = Date.from(Instant.ofEpochSecond(now.getEpochSecond))
    val claimsSet = new JWTClaimsSet.Builder()
      .issuer(conf.teamId.team)
      .issueTime(issuedAt)
      .build()
    val signable = new SignedJWT(jwtHeader, claimsSet)
    signable.sign(signer)
    signable
  }

  private def headerValue(signed: SignedJWT) = {
    val serialized = signed.serialize()
    s"bearer $serialized"
  }

  // drops 'begin private key...', 'end private key...' boiler
  private def readKey(file: Path) = {
    val src = Source.fromFile(file.toFile)
    try src.getLines().toList.drop(1).init.mkString
    finally src.close()
  }

  override def close(): Unit = client.close()
}
