package com.malliina.push.apns

import cats.Monad
import cats.implicits._
import com.malliina.http.HttpClient

object APNSHttpClientF {
  def apply[F[_]: Monad](conf: APNSTokenConf, http: HttpClient[F], isSandbox: Boolean) =
    new APNSHttpClientF[F](http, TokenBuilder.token(conf), isSandbox)
}

class APNSHttpClientF[F[_]: Monad](http: HttpClient[F], prep: TokenBuilder, isSandbox: Boolean)
  extends APNSHttpClientBase[F](http, prep, isSandbox) {
  override def push(id: APNSToken, message: APNSRequest): F[Either[APNSError, APNSIdentifier]] =
    send(id, message).map(parseResponse)

  override def pushAll(
    ids: Seq[APNSToken],
    message: APNSRequest
  ): F[Seq[Either[APNSError, APNSIdentifier]]] =
    ids.traverse { token => push(token, message) }
}
