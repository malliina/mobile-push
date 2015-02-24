package com.mle.push.mpns

import java.io.StringWriter

import com.mle.concurrent.ExecutionContexts.cached
import com.mle.http.AsyncHttp
import com.mle.push.PushClient
import com.mle.util.Log
import com.ning.http.client.{Response => NingResponse}

import scala.concurrent.Future
import scala.xml.{Elem, XML}

/**
 *
 * @author mle
 */
class MPNSClient extends PushClient[MPNSMessage, NingResponse] with Log {
  override def pushAll(urls: Seq[String], message: MPNSMessage): Future[Seq[NingResponse]] = {
    val bodyAsString = serialize(message.xml)
    sendMulti(urls, bodyAsString, message.headers)
  }

  /**
   * Might throw [[NullPointerException]] if `url` is bogus, but how do you solidly validate a URL in Java? I don't
   * know.
   *
   * @param url device URL
   * @param message content
   * @return
   */
  override def push(url: String, message: MPNSMessage): Future[NingResponse] = send(url, message.xml, message.headers)

  protected def send(url: String, xml: Elem, headers: Map[String, String]): Future[NingResponse] =
    sendSingle(url, serialize(xml), headers)

  private def sendMulti(urls: Seq[String], body: String, headers: Map[String, String]) =
    Future.sequence(urls.map(url => sendSingle(url, body, headers)))

  private def sendSingle(url: String, body: String, headers: Map[String, String]) =
    AsyncHttp.post(url, body, headers)

  /**
   * Serializes `elem` to a string, adding an xml declaration to the top. Encodes the payload
   * automatically as described in
   * http://msdn.microsoft.com/en-us/library/windowsphone/develop/hh202945(v=vs.105).aspx.
   *
   * @param elem xml
   * @return xml as a string
   */
  private def serialize(elem: Elem) = {
    val writer = new StringWriter
    // xmlDecl = true prepends this as the first line, as desired: <?xml version="1.0" encoding="utf-8"?>
    XML.write(writer, elem, "UTF-8", xmlDecl = true, doctype = null)
    val str = writer.toString
    str
  }
}

object MPNSClient {
  //  val MessageID = "X-MessageID"
  // request headers
  val XNotificationClass = "X-NotificationClass"
  val NotificationType = "X-WindowsPhone-Target"
  // response headers
  val NotificationStatus = "X-NotificationStatus"
  val SubscriptionStatus = "X-SubscriptionStatus"
  val DeviceConnectionStatus = "X-DeviceConnectionStatus"

  // not a bug
  val TILE = "token"
  val TOAST = "toast"
  val TILE_IMMEDIATE = "1"
  val TOAST_IMMEDIATE = "2"
  val RAW_IMMEDIATE = "3"

  val CONTENT_TYPE = "Content-Type"
  val TEXT_XML = "text/xml"

  private def baseHeaders(notificationClass: String) = Map(
    CONTENT_TYPE -> TEXT_XML,
    XNotificationClass -> notificationClass)

  private def headers(notificationType: String, notificationClass: String) =
    baseHeaders(notificationClass) ++ Map(NotificationType -> notificationType)

  val toastHeaders = headers(TOAST, TOAST_IMMEDIATE)

  val tileHeaders = headers(TILE, TILE_IMMEDIATE)

  val rawHeaders = baseHeaders(RAW_IMMEDIATE)
}



