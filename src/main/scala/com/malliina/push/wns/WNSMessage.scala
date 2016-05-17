package com.malliina.push.wns

import com.malliina.push.WindowsMessage
import com.malliina.push.wns.WNSClient.{CachePolicy, Tag, Ttl, WnsType}

import scala.concurrent.duration.Duration

trait WNSMessage extends WindowsMessage {
  def wnsType: WNSType

  def cache: Boolean

  def ttl: Option[Duration]

  def tag: Option[String]

  override def headers: Map[String, String] = ttlHeaders ++ tagHeaders ++ Map(
    WnsType -> wnsType.name,
    CachePolicy -> cacheHeaderValue
  )

  def cacheHeaderValue = if (cache) "true" else "false"

  private def ttlHeaders =
    ttl.map(dur => Map(Ttl -> s"${dur.toSeconds}")) getOrElse Map.empty

  private def tagHeaders =
    tag.map(t => Map(Tag -> t)) getOrElse Map.empty
}

object WNSMessage {

}
