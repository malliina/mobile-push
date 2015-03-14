package com.mle.push.gcm

import play.api.libs.json.Json

/**
 * @author Michael
 */
case class MappedGCMResponse(ids: Seq[String], response: GCMResponse)

object MappedGCMResponse {
  implicit val json = Json.reads[MappedGCMResponse]
}
