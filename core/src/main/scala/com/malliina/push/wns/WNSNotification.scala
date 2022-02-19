package com.malliina.push.wns

trait WNSNotification {
  def payload: String

  def notificationType: NotificationType

  def isRaw: Boolean = notificationType == NotificationType.Raw
}
