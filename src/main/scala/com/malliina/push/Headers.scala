package com.malliina.push

import okhttp3.MediaType

object Headers extends Headers

trait Headers {
  val Authorization = "Authorization"
  val ContentType = "Content-Type"
  val FormType = "application/x-www-form-urlencoded"
  val JsonType = "application/json"

  val OctetStream = "application/octet-stream"
  val TextHtml = "text/xml"

  val XmlMediaType = MediaType.parse("application/xml")
  val XmlMediaTypeUtf8 = MediaType.parse("application/xml; charset=utf-8")
}
