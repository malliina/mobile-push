package com.mle.push.apns

import java.security.KeyStore

import com.mle.push.PushClient
import com.notnoop.apns.{APNS, ApnsNotification}
import play.api.libs.json.Json

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author Michael
 */
class APNSClient(keyStore: KeyStore, keyStorePass: String, isSandbox: Boolean = false)
  extends PushClient[APNSMessage, ApnsNotification] with AutoCloseable {

  private val builder = APNS.newService().withCert(keyStore, keyStorePass)
  val service =
    if (isSandbox) builder.withSandboxDestination().build()
    else builder.withProductionDestination().build()

  override def push(id: String, message: APNSMessage): Future[ApnsNotification] = {
    Future(service.push(id, stringify(message)))
  }

  override def pushAll(ids: Seq[String], message: APNSMessage): Future[Seq[ApnsNotification]] = {
    Future(service.push(ids, stringify(message)).toSeq)
  }

  override def close(): Unit = service.stop()

  private def stringify(message: APNSMessage) = Json stringify (Json toJson message)
}
