package com.malliina

import com.malliina.json.PrimitiveFormats
import io.circe.Codec

import scala.concurrent.duration.{Duration, FiniteDuration}

package object push {
  implicit val durationCodec: Codec[Duration] = PrimitiveFormats.durationCodec
}
