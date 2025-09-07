package com.malliina.push.fcm

import cats.Monad
import com.malliina.http.{HttpClient, OkHttpHttpClient}
import com.malliina.push.gcm.GoogleClientF

object FCMClientF {
  def apply[F[_]: Monad](apiKey: String, http: OkHttpHttpClient[F]): GoogleClientF[F] =
    new GoogleClientF[F](apiKey, FCMLegacyClient.FcmEndpoint, http)
}
