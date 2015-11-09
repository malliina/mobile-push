package com.mle.push

/**
  * @author mle
  */
trait Token {
  def token: String

  override def toString: String = token
}
