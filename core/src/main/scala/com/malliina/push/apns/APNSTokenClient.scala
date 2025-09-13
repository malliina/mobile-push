package com.malliina.push.apns

import com.malliina.http.OkClient

object APNSTokenClient {
  def default: APNSHttpClient =
    apply(APNSTokenConf.default.toOption.get, OkClient.default, isSandbox = false)

  def apply(conf: APNSTokenConf, http: OkClient, isSandbox: Boolean): APNSHttpClient =
    new APNSHttpClient(http, isSandbox)
}
