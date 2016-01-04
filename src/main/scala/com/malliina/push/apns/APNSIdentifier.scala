package com.malliina.push.apns

import com.malliina.push.SimpleCompanion

/**
  * @author mle
  */
case class APNSIdentifier(id: String) {
  override def toString = id
}

object APNSIdentifier extends SimpleCompanion[String, APNSIdentifier] {
  override def write(t: APNSIdentifier): String = t.id
}
