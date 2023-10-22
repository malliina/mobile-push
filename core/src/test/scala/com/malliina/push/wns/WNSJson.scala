package com.malliina.push.wns

import com.malliina.push.wns.CommandId.Dismiss
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax.EncoderOps

class WNSJson extends munit.FunSuite {
  test("read") {
    val in = """"dismiss""""
    val result = parse(in).getOrElse(Json.Null).as[CommandId].toOption
    assert(result.contains(Dismiss))
  }

  test("write") {
    val str = (Dismiss: CommandId).asJson.noSpaces
    assert(str == """"dismiss"""")
  }
}
