package com.mle.push

import scala.concurrent.Future

/**
 * @tparam T type of message
 * @tparam U type of response
 */
trait PushClient[T, U] {
  def push(id: String, message: T): Future[U]

  def pushAll(ids: Seq[String], message: T): Future[Seq[U]]
}
