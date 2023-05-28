package com.malliina.push

import scala.concurrent.Future

/** @tparam S
  *   type of token
  * @tparam T
  *   type of message
  * @tparam U
  *   type of response
  */
trait PushClient[S, T, U] extends PushClientF[S, T, U, Future] {
  def push(id: S, message: T): Future[U]
  def pushAll(ids: Seq[S], message: T): Future[Seq[U]]
}

trait PushClientF[S, T, U, F[_]] {
  def push(id: S, message: T): F[U]
  def pushAll(ids: Seq[S], message: T): F[Seq[U]]
}
