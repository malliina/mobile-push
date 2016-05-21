package com.malliina.push.wns

import com.malliina.push.WindowsClient

trait XmlNotification extends WNSNotification with Xmlable {
  override def payload: String = WindowsClient.serialize(xml)
}
