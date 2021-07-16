package com.malliina.push.apns

import java.security.KeyStore

import com.malliina.push.PushClient
import com.malliina.push.apns.APNSClient.stringify
import com.notnoop.apns.{APNS, ApnsNotification, ApnsService}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

@deprecated("Use APNSHttpClient instead", "1.15.0")
class APNSClient(keyStore: KeyStore, keyStorePass: String, isSandbox: Boolean = false)(implicit
  ec: ExecutionContext
) extends PushClient[APNSToken, APNSMessage, ApnsNotification]
  with AutoCloseable {

  private val builder = APNS.newService().withCert(keyStore, keyStorePass)
  val service: ApnsService =
    if (isSandbox) builder.withSandboxDestination().build()
    else builder.withProductionDestination().build()

  override def push(id: APNSToken, message: APNSMessage): Future[ApnsNotification] =
    Future(service.push(id.token, stringify(message)))

  override def pushAll(ids: Seq[APNSToken], message: APNSMessage): Future[Seq[ApnsNotification]] =
    Future(service.push(ids.map(_.token).asJava, stringify(message)).asScala.toList)

  /** "When a remote notification cannot be delivered because the intended app does not exist on the device, the feedback
    * service adds that device’s token to its list."
    *
    * "Query the feedback service daily to get the list of device tokens. Use the timestamp to verify that the device
    * tokens haven’t been reregistered since the feedback entry was generated. For each device that has not been
    * reregistered, stop sending notifications."
    *
    * "The feedback service’s list is cleared after you read it. Each time you connect to the feedback service, the
    * information it returns lists only the failures that have happened since you last connected."
    *
    * @see https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/CommunicatingWIthAPS.html
    */
  def inactiveDevices: Future[Seq[InactiveDevice]] = Future {
    service.getInactiveDevices.asScala.toList.map { case (hexID, asOf) =>
      InactiveDevice(hexID, asOf.getTime)
    }
  }

  override def close(): Unit = service.stop()
}

object APNSClient {
  def stringify(message: APNSMessage): String = message.asJson.toString
}
