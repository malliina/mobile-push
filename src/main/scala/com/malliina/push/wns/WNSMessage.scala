package com.malliina.push.wns

import com.malliina.push.wns.WNSClient.{CachePolicy, Tag, Ttl, WnsType}

import scala.concurrent.duration.Duration

case class WNSMessage(notification: WNSNotification,
                      cache: Option[Boolean] = None,
                      ttl: Option[Duration] = None,
                      tag: Option[String] = None) {

  def payload: String = notification.payload

  def headers: Map[String, String] =
    ttlHeaders ++ tagHeaders ++ cacheHeaders ++
      Map(WnsType -> notification.notificationType.name)

  private def cacheHeaders =
    mappify(CachePolicy, cache.map(c => if(c) "cache" else "no-cache"))

  private def ttlHeaders =
    mappify(Ttl, ttl.map(_.toSeconds.toString))

  private def tagHeaders =
    mappify(Tag, tag)

  private def mappify(key: String, value: Option[String]) =
    value.map(v => Map(key -> v)) getOrElse Map.empty
}
