package com.malliina.push.gcm

import com.malliina.push.gcm.GCMResult.GCMResultError
import com.malliina.push.gcm.MappedGCMResponse.TokenReplacement
import play.api.libs.json.Json

case class MappedGCMResponse(ids: Seq[GCMToken], response: GCMResponse) {
  lazy val replacements: Seq[TokenReplacement] = {
    if (response.canonical_ids > 0) {
      for {
        (id, result) <- ids zip response.results
        canonical <- result.registration_id.toSeq
      } yield TokenReplacement(id, GCMToken(canonical))
    } else {
      Nil
    }
  }
  lazy val uninstalled: Seq[GCMToken] = failedIDs(GCMResult.NotRegistered)

  def failedIDs(desiredError: GCMResultError) = {
    if (response.failure > 0) {
      for {
        (id, result) <- ids zip response.results
        error <- result.error.toSeq if error == desiredError
      } yield id
    } else {
      Nil
    }
  }
}

object MappedGCMResponse {
  implicit val json = Json.reads[MappedGCMResponse]

  case class TokenReplacement(oldToken: GCMToken, newToken: GCMToken)

}
