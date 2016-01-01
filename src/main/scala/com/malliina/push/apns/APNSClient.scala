package com.malliina.push.apns

import java.security.KeyStore

import com.malliina.push.PushClient
import com.malliina.push.apns.APNSClient.stringify
import com.notnoop.apns.{APNS, ApnsNotification}
import play.api.libs.json.Json

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * @author Michael
 */
class APNSClient(keyStore: KeyStore, keyStorePass: String, isSandbox: Boolean = false)
  extends PushClient[APNSToken, APNSMessage, ApnsNotification] with AutoCloseable {

  private val builder = APNS.newService().withCert(keyStore, keyStorePass)
  val service =
    if (isSandbox) builder.withSandboxDestination().build()
    else builder.withProductionDestination().build()

  override def push(id: APNSToken, message: APNSMessage): Future[ApnsNotification] = {
    Future(service.push(id.token, stringify(message)))
  }

  override def pushAll(ids: Seq[APNSToken], message: APNSMessage): Future[Seq[ApnsNotification]] = {
    Future(service.push(ids.map(_.token), stringify(message)).toSeq)
  }

  /**
   * "When a remote notification cannot be delivered because the intended app does not exist on the device, the feedback
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
  def inactiveDevices: Future[Seq[InactiveDevice]] = Future(service.getInactiveDevices.toSeq.map {
    case (hexID, asOf) => InactiveDevice(hexID, asOf.getTime)
  })

  override def close(): Unit = service.stop()
}

object APNSClient {
  def stringify(message: APNSMessage) = Json stringify (Json toJson message)
}
