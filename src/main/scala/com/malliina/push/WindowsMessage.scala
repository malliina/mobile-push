package com.malliina.push

import scala.xml.Elem

trait WindowsMessage {
  def xml: Elem

  def headers: Map[String, String]
}
