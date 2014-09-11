package com.mle.push.gcm

import play.api.libs.json.Json

/**
 * @param id the GCM registration ID
 * @param tag custom app-provided tag
 */
case class AndroidDevice(id: String, tag: String)

object AndroidDevice {
  implicit val json = Json.format[AndroidDevice]
}