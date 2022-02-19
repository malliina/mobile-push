package com.malliina.push.wns

trait Named extends Any {
  def name: String
  override def toString: String = name
}
