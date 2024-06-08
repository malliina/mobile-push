package com.malliina.push.fcm

import com.malliina.http.{FullUrl, HttpClient}
import com.malliina.push.fcm.FCMLegacyClient.FcmEndpoint
import com.malliina.push.gcm.GoogleClient

import scala.concurrent.{ExecutionContext, Future}

object FCMLegacyClient {
  val FcmEndpoint: FullUrl = FullUrl.https("fcm.googleapis.com", "/fcm/send")

  def apply(apiKey: String, http: HttpClient[Future], ec: ExecutionContext): FCMLegacyClient =
    new FCMLegacyClient(apiKey, http, ec)
}

class FCMLegacyClient(apiKey: String, http: HttpClient[Future], ec: ExecutionContext)
  extends GoogleClient(apiKey, FcmEndpoint, http)(ec)
