package com.malliina.push.fcm

import com.malliina.http.{FullUrl, SimpleHttpClient}
import com.malliina.push.fcm.FCMLegacyClient.FcmEndpoint
import com.malliina.push.gcm.GoogleClient

import scala.concurrent.{ExecutionContext, Future}

object FCMLegacyClient {
  val FcmEndpoint: FullUrl = FullUrl.https("fcm.googleapis.com", "/fcm/send")

  def apply(apiKey: String, http: SimpleHttpClient[Future], ec: ExecutionContext): FCMLegacyClient =
    new FCMLegacyClient(apiKey, http, ec)
}

class FCMLegacyClient(apiKey: String, http: SimpleHttpClient[Future], ec: ExecutionContext)
  extends GoogleClient(apiKey, FcmEndpoint, http)(ec)
