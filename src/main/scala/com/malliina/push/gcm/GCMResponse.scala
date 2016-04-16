package com.malliina.push.gcm

import play.api.libs.json.Json

case class GCMResponse(multicast_id: Long,
                       success: Int,
                       failure: Int,
                       canonical_ids: Int,
                       results: Seq[GCMResult])

object GCMResponse {
  implicit val json = Json.reads[GCMResponse]
}
