package com.malliina.push.apns

import io.circe._
import io.circe.generic.semiauto._

case class APNSRequest(message: APNSMessage, meta: APNSMeta)

object APNSRequest {
  implicit val json: Codec[APNSRequest] = deriveCodec[APNSRequest]

  def withTopic(topic: APNSTopic, message: APNSMessage) =
    APNSRequest(message, APNSMeta.withTopic(topic))
}
