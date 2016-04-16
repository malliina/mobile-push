package com.malliina.push.apns

import com.malliina.push.SimpleCompanion

case class APNSTopic(topic: String)

object APNSTopic extends SimpleCompanion[String, APNSTopic] {
  override def write(t: APNSTopic): String = t.topic
}
