package com.malliina.push.wns

import com.malliina.push.WindowsMessage
import com.malliina.push.wns.WNSClient.{CachePolicy, Tag, Ttl, WnsType}

import scala.concurrent.duration.Duration
import scala.xml.Elem

case class WNSMessage(body: Xmlable,
                      wnsType: WNSType,
                      cache: Boolean,
                      ttl: Option[Duration] = None,
                      tag: Option[String] = None) extends WindowsMessage {

  override def xml: Elem = body.xml

  override def headers: Map[String, String] = ttlHeaders ++ tagHeaders ++ Map(
    WnsType -> wnsType.name,
    CachePolicy -> cacheHeaderValue
  )

  def cacheHeaderValue = if (cache) "cache" else "no-cache"

  private def ttlHeaders =
    ttl.map(dur => Map(Ttl -> s"${dur.toSeconds}")) getOrElse Map.empty

  private def tagHeaders =
    tag.map(t => Map(Tag -> t)) getOrElse Map.empty
}
