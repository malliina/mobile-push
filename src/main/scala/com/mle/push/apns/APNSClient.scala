package com.mle.push.apns

import java.security.KeyStore

import com.mle.push.PushClient
import com.notnoop.apns.{APNS, ApnsNotification}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author Michael
 */
class APNSClient(keyStore: KeyStore, keyStorePass: String) extends PushClient[APNSMessage, ApnsNotification] with AutoCloseable {
  val service = APNS.newService().withCert(keyStore, keyStorePass).withSandboxDestination().build()

  override def send(id: String, dest: APNSMessage): Future[ApnsNotification] = {
    Future(service.push(id, Json stringify (Json toJson dest)))
  }

  override def close(): Unit = service.stop()
}
