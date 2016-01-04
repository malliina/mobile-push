package com.malliina.push.apns

import play.api.libs.json.Json

/**
  * @author mle
  */
case class APNSMeta(apnsTopic: APNSTopic,
                    apnsExpiration: Long,
                    apnsPriority: APNSPriority,
                    apnsId: Option[APNSIdentifier])

object APNSMeta {
  implicit val json = Json.format[APNSMeta]

  def withTopic(topic: APNSTopic) = APNSMeta(topic, 0, APNSImmediately, None)
}
