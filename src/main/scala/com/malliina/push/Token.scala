package com.malliina.push

trait Token extends Any {
  def token: String
  override def toString: String = token
}
