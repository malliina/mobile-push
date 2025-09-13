package com.malliina.push.mpns

import com.malliina.http.{HttpResponse, SimpleHttpClient}
import com.malliina.push.{Headers, WindowsClient, WindowsMessage}

import scala.concurrent.{ExecutionContext, Future}

class MPNSClient(http: SimpleHttpClient[Future], ec: ExecutionContext)
  extends WindowsClient[MPNSToken, WindowsMessage](http)(ec) {

  /** Might throw [[NullPointerException]] if `url` is bogus, but how do you solidly validate a URL
    * in Java? I don't know.
    *
    * @param url
    *   device URL
    * @param message
    *   content
    * @return
    */
  override def push(url: MPNSToken, message: WindowsMessage): Future[HttpResponse] =
    send(url, message.xml, message.headers)
}

object MPNSClient {
  def isTokenValid(token: String): Boolean = MPNSToken.isValid(token)

  //  val MessageID = "X-MessageID"
  // request headers
  val XNotificationClass = "X-NotificationClass"
  val NotificationType = "X-WindowsPhone-Target"
  // response headers
  val NotificationStatus = "X-NotificationStatus"
  val SubscriptionStatus = "X-SubscriptionStatus"
  val DeviceConnectionStatus = "X-DeviceConnectionStatus"

  // not a bug
  val Tile = "token"
  val Toast = "toast"
  val TileImmediate = "1"
  val ToastImmediate = "2"
  val RawImmediate = "3"

  private def baseHeaders(notificationClass: String) =
    Map(Headers.ContentType -> Headers.TextHtml, XNotificationClass -> notificationClass)

  private def headers(notificationType: String, notificationClass: String) =
    baseHeaders(notificationClass) ++ Map(NotificationType -> notificationType)

  val toastHeaders = headers(Toast, ToastImmediate)

  val tileHeaders = headers(Tile, TileImmediate)

  val rawHeaders = baseHeaders(RawImmediate)
}
