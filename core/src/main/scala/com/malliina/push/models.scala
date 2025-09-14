package com.malliina.push

import scala.xml.Elem

trait Token extends Any {
  def token: String
  override def toString: String = token
}

trait TokenCompanion[T <: Token] extends SimpleCompanion[String, T] {
  override def write(t: T): String = t.token
}

trait WindowsMessage {
  def xml: Elem
  def headers: Map[String, String]
}

object OAuthKeys extends OAuthKeys

trait OAuthKeys {
  val ClientCredentials = "client_credentials"
  val ClientId = "client_id"
  val ClientSecret = "client_secret"
  val GrantType = "grant_type"
  val Scope = "scope"
}

object Headers extends Headers

trait Headers {
  val Authorization = "Authorization"
  val ContentType = "Content-Type"
  val FormType = "application/x-www-form-urlencoded"
  val JsonType = "application/json"

  val OctetStream = "application/octet-stream"
  val TextHtml = "text/xml"

  val XmlMediaType = "application/xml"
  val XmlMediaTypeUtf8 = "application/xml; charset=utf-8"
}
