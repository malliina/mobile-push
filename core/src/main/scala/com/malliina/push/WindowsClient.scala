package com.malliina.push

import com.malliina.http.{FullUrl, HttpResponse, SimpleHttpClient}

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.{Elem, XML}

abstract class WindowsClient[T <: Token, M <: WindowsMessage](http: SimpleHttpClient[Future])(
  implicit ec: ExecutionContext
) extends PushClient[T, M, HttpResponse] {
  override def pushAll(urls: Seq[T], message: M): Future[Seq[HttpResponse]] = {
    val bodyAsString = WindowsClient.serialize(message.xml)
    sendMulti(urls, bodyAsString, message.headers)
  }

  protected def send(url: T, xml: Elem, headers: Map[String, String]): Future[HttpResponse] =
    sendSingle(url, WindowsClient.serialize(xml), headers)

  protected def sendMulti(
    urls: Seq[T],
    body: String,
    headers: Map[String, String]
  ): Future[Seq[HttpResponse]] =
    Future.sequence(urls.map(url => sendSingle(url, body, headers)))

  protected def sendSingle(
    url: T,
    body: String,
    headers: Map[String, String]
  ): Future[HttpResponse] =
    http.postString(
      FullUrl.build(url.token).toOption.get,
      body,
      Headers.XmlMediaTypeUtf8.toString,
      headers
    )
}

object WindowsClient {

  /** Serializes `elem` to a string, adding an xml declaration to the top. Encodes the payload
    * automatically as described in
    * http://msdn.microsoft.com/en-us/library/windowsphone/develop/hh202945(v=vs.105).aspx.
    *
    * @param elem
    *   xml
    * @return
    *   xml as a string
    */
  def serialize(elem: Elem): String = {
    val writer = new StringWriter
    // xmlDecl = true prepends this as the first line, as desired: <?xml version="1.0" encoding="utf-8"?>
    XML.write(writer, elem, "UTF-8", xmlDecl = true, doctype = null)
    writer.toString
  }
}
