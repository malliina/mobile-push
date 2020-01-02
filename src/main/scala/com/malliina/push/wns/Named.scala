package com.malliina.push.wns

trait Named {
  def name: String
  override def toString: String = name
}
