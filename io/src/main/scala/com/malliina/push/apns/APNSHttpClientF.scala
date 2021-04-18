package com.malliina.push.apns

import cats.Monad
import cats.implicits._
import com.malliina.http.HttpClient

class APNSHttpClientF[F[+_]: Monad](http: HttpClient[F], prep: RequestPreparer, isSandbox: Boolean)
  extends APNSHttpClientBase[F](http, prep, isSandbox) {
  override def push(id: APNSToken, message: APNSRequest): F[Either[APNSError, APNSIdentifier]] =
    send(id, message).map(parseResponse)

  override def pushAll(
    ids: Seq[APNSToken],
    message: APNSRequest
  ): F[Seq[Either[APNSError, APNSIdentifier]]] =
    ids.toList.traverse { token => push(token, message) }
}
