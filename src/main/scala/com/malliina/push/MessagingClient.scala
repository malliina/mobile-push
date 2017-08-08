package com.malliina.push

import com.malliina.concurrent.FutureOps
import com.malliina.http.WebResponse
import com.malliina.push.MessagingClient.log
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

/**
  * @tparam T type of device
  */
trait MessagingClient[T] {
  def send(dest: T): Future[WebResponse]

  def sendLogged(dest: T)(implicit ec: ExecutionContext): Future[Unit] = send(dest)
    .map(r => log info s"Sent message to: $dest. Response: ${r.code}")
    .recoverAll(t => log.warn(s"Unable to send message to: $dest", t))
}

object MessagingClient {
  private val log = LoggerFactory.getLogger(getClass)
}
