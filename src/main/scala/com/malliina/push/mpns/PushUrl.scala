package com.malliina.push.mpns

import io.circe._
import io.circe.generic.semiauto._

/** The same device may open different push URLs at different points in time, however the old ones still work until the
  * channel is closed, it seems. We only accept one push URL per device however, otherwise the same push notification
  * is sent multiple times to the same device. Therefore we need another way of uniquely identifying a device, and that
  * is the tag.
  *
  * @param url device token
  * @param silent true for a silent notification, otherwise false
  * @param tag tag that IDs the device
  */
case class PushUrl(url: MPNSToken, silent: Boolean, tag: String)

object PushUrl {
  implicit val json: Codec[PushUrl] = deriveCodec[PushUrl]
}
