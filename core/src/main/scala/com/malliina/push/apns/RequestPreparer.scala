package com.malliina.push.apns

import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import okhttp3.Request

import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.atomic.AtomicReference

trait RequestPreparer {
  def prepare(request: Request.Builder): Request.Builder
}

object RequestPreparer {
  def noop: RequestPreparer = (request: Request.Builder) => request
  def token(conf: APNSTokenConf): RequestPreparer = APNSTokenPreparer(conf)
}

object APNSTokenPreparer {
  def apply(conf: APNSTokenConf): APNSTokenPreparer = new APNSTokenPreparer(conf)
}

/** <p>For security, APNs requires you to refresh your token regularly. Refresh your token no more
  * than once every 20 minutes and no less than once every 60 minutes. APNs rejects any request
  * whose token contains a timestamp that is more than one hour old. Similarly, APNs reports an
  * error if you recreate your tokens more than once every 20 minutes.</p>
  *
  * <p>On your provider server, set up a recurring task to recreate your token with a current
  * timestamp. Encrypt the token again and attach it to subsequent notification requests.</p>
  *
  * @see
  *   https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/CommunicatingwithAPNs.html
  */
class APNSTokenPreparer(conf: APNSTokenConf) extends RequestPreparer {
  val keyFactory = KeyFactory.getInstance("EC")
  val key = keyFactory.generatePrivate(conf.privateKey).asInstanceOf[ECPrivateKey]
  val signer = new ECDSASigner(key)
  val jwtHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(conf.keyId.id).build()

  private val providerToken: AtomicReference[SignedJWT] =
    new AtomicReference[SignedJWT](newProviderToken(Instant.now()))

  override def prepare(request: Request.Builder): Request.Builder =
    request.header("authorization", tokenHeader())

  private def tokenHeader() = headerValue(validToken(Instant.now()))

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
}
