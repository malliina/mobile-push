package com.malliina.push.fcm

import cats.Monad
import com.malliina.http.HttpClient
import com.malliina.push.gcm.GoogleClientF

object FCMClientF {
  def apply[F[_]: Monad](apiKey: String, http: HttpClient[F]): GoogleClientF[F] =
    new GoogleClientF[F](apiKey, FCMLegacyClient.FcmEndpoint, http)
}
