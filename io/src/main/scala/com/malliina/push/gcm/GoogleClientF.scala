package com.malliina.push.gcm

import cats.Monad
import cats.implicits._
import com.malliina.http.{FullUrl, HttpClient}
import com.malliina.push.gcm.GCMClient.{MaxRecipientsPerRequest, parseOrFail}

class GoogleClientF[F[+_]: Monad](apiKey: String, postEndpoint: FullUrl, http: HttpClient[F])
  extends GoogleClientBase[F](apiKey, postEndpoint, http) {

  def push(id: GCMToken, message: GCMMessage): F[MappedGCMResponse] =
    sendLimitedMapped(Seq(id), message)

  def pushAll(ids: Seq[GCMToken], message: GCMMessage): F[Seq[MappedGCMResponse]] =
    ids.grouped(MaxRecipientsPerRequest).toList.traverse { batch =>
      sendLimitedMapped(batch, message)
    }

  private def sendLimitedMapped(
    ids: Seq[GCMToken],
    message: GCMMessage
  ): F[MappedGCMResponse] =
    sendLimited(ids, message).map { r => MappedGCMResponse(ids, parseOrFail(r, postEndpoint)) }
}
