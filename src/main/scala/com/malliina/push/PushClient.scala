package com.malliina.push

import scala.concurrent.Future

/**
  * @tparam S type of token
  * @tparam T type of message
  * @tparam U type of response
  */
trait PushClient[S, T, U] {
  def push(id: S, message: T): Future[U]
  def pushAll(ids: Seq[S], message: T): Future[Seq[U]]
}
