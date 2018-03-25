package com.malliina.push

import okhttp3.MediaType

object Headers extends Headers

trait Headers {
  val Authorization = "Authorization"

  val OctetStream = "application/octet-stream"
  val FormType = "application/x-www-form-urlencoded"
  val JsonType = "application/json"
  val TextHtml = "text/xml"

  val ContentType = "Content-Type"
  val XmlMediaType = MediaType.parse("application/xml")
  val XmlMediaTypeUtf8 = MediaType.parse("application/xml; charset=utf-8")
}
