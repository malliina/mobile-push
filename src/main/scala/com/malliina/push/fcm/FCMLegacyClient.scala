package com.malliina.push.fcm

import com.malliina.http.FullUrl
import com.malliina.push.fcm.FCMLegacyClient.FcmEndpoint
import com.malliina.push.gcm.GoogleClient

object FCMLegacyClient {
  val FcmEndpoint = FullUrl.https("fcm.googleapis.com", "/fcm/send")

  def apply(apiKey: String): FCMLegacyClient = new FCMLegacyClient(apiKey)
}

class FCMLegacyClient(apiKey: String) extends GoogleClient(apiKey, FcmEndpoint)
