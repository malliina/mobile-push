package com.mle.push.gcm

import play.api.libs.json.Json

/**
 * @author Michael
 */
case class GCMResponse(multicast_id: Long, success: Int, failure: Int, canonical_ids: Int, results: Seq[GCMResult])

object GCMResponse {
  implicit val json = Json.reads[GCMResponse]
}
