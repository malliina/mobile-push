package com.malliina.push

trait Token {
  def token: String

  override def toString: String = token
}
