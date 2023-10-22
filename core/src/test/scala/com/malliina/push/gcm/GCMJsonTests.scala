package com.malliina.push.gcm

import com.malliina.push.gcm.MappedGCMResponse.TokenReplacement
import com.malliina.push.gcm.{GCMResponse, GCMResult, GCMResultError, GCMToken, MappedGCMResponse}
import io.circe.Json
import io.circe.parser.parse

class GCMJsonTests extends munit.FunSuite {
  test("GCM responses") {
    val exampleResponse =
      """{ "multicast_id": 216,
        |  "success": 3,
        |  "failure": 3,
        |  "canonical_ids": 1,
        |  "results": [
        |    { "message_id": "1:0408" },
        |    { "error": "Unavailable" },
        |    { "error": "InvalidRegistration" },
        |    { "message_id": "1:1516" },
        |    { "message_id": "1:2342", "registration_id": "32" },
        |    { "error": "NotRegistered"}
        |  ]
        |}""".stripMargin
    val parsed = parse(exampleResponse).getOrElse(Json.Null).as[GCMResponse].toOption.get
    val expected = GCMResponse(
      216,
      3,
      3,
      1,
      Seq(
        GCMResult(Some("1:0408"), None, None),
        GCMResult(None, None, Some(GCMResultError.Unavailable)),
        GCMResult(None, None, Some(GCMResultError.InvalidRegistration)),
        GCMResult(Some("1:1516"), None, None),
        GCMResult(Some("1:2342"), Some("32"), None),
        GCMResult(None, None, Some(GCMResultError.NotRegistered))
      )
    )
    assert(parsed.multicast_id == 216)
    assert(parsed == expected)

    val mapped = MappedGCMResponse(Seq(1, 2, 3, 4, 5, 6).map(n => GCMToken(n.toString)), parsed)
    assert(mapped.replacements == Seq(TokenReplacement(GCMToken("5"), GCMToken("32"))))
    assert(mapped.uninstalled == Seq(GCMToken("6")))
  }
}
