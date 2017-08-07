package com.malliina.push

import com.malliina.concurrent.FutureOps
import com.malliina.push.MessagingClient.log
import org.apache.http.HttpResponse
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

/**
  * @tparam T type of device
  */
trait MessagingClient[T] {
  def send(dest: T): Future[HttpResponse]

  def sendLogged(dest: T)(implicit ec: ExecutionContext): Future[Unit] = send(dest)
    .map(r => log info s"Sent message to: $dest. Response: ${r.getStatusLine.getReasonPhrase}")
    .recoverAll(t => log.warn(s"Unable to send message to: $dest", t))
}

object MessagingClient {
  private val log = LoggerFactory.getLogger(getClass)
}
