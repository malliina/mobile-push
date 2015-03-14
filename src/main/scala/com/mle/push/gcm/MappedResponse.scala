package com.mle.push.gcm

import com.ning.http.client.Response

/**
 * @author Michael
 */

case class MappedResponse(ids: Seq[String], response: Response) {
  def parsed = MappedGCMResponse(ids, GCMClient.parseOrFail(response))
}
