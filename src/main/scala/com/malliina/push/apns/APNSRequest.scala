package com.malliina.push.apns

import play.api.libs.json.Json

case class APNSRequest(message: APNSMessage, meta: APNSMeta)

object APNSRequest {
  implicit val json = Json.format[APNSRequest]

  def withTopic(topic: APNSTopic, message: APNSMessage) =
    APNSRequest(message, APNSMeta.withTopic(topic))
}
