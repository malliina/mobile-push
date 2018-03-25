package com.malliina

import com.malliina.json.PrimitiveFormats
import play.api.libs.json.Format

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

package object push {
  implicit val ec: ExecutionContext = Execution.cached
  implicit val durationFormat: Format[Duration] = PrimitiveFormats.durationFormat
}
