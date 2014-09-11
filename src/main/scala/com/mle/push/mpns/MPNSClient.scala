package com.mle.push.mpns

import java.io.StringWriter

import com.mle.http.AsyncHttp
import com.mle.push.PushClient
import com.mle.util.Log
import com.mle.util.Utils.executionContext
import com.ning.http.client.{Response => NingResponse}

import scala.concurrent.Future
import scala.xml.{Elem, XML}

/**
 *
 * @author mle
 */
class MPNSClient extends PushClient[ToastMessage] with Log {
  //  val MessageID = "X-MessageID"
  // request headers
  val XNotificationClass = "X-NotificationClass"
  val NotificationType = "X-WindowsPhone-Target"
  // response headers
  val NotificationStatus = "X-NotificationStatus"
  val SubscriptionStatus = "X-SubscriptionStatus"
  val DeviceConnectionStatus = "X-DeviceConnectionStatus"

  val TILE = "tile"
  val TOAST = "toast"
  val RAW = "raw"
  val IMMEDIATE = "2"

  val CONTENT_TYPE = "Content-Type"
  val TEXT_XML = "text/xml"

  private val toastHeaders = Map(
    CONTENT_TYPE -> TEXT_XML,
    NotificationType -> TOAST,
    XNotificationClass -> IMMEDIATE)

  def send(url: String, message: ToastMessage): Future[NingResponse] = send(url, toastXml(message))

  protected def send(url: String, xml: Elem): Future[NingResponse] =
    AsyncHttp.post(url, serialize(xml), toastHeaders)

  private def toastXml(message: ToastMessage): Elem =
    ToastPayload.toastXml(message.text1, message.text2, message.deepLink, message.silent)

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
    //    log.info(str)
    str
  }
}

case class ToastMessage(text1: String, text2: String, deepLink: String, silent: Boolean)

