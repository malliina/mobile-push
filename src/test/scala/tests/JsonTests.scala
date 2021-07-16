package tests

import com.malliina.push.apns.{APNSMessage, APSPayload, AlertPayload}
import com.malliina.push.gcm.MappedGCMResponse.TokenReplacement
import com.malliina.push.gcm.{GCMResponse, GCMResult, GCMResultError, GCMToken, MappedGCMResponse}
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.parser._

class JsonTests extends munit.FunSuite {
  test("can json this") {
    val obj = Map[String, String]("a" -> "b").asJson
    val a = obj.hcursor.downField("a").as[String]
    assertEquals(a.toOption.get, "b")
  }

  test("APNS serialization") {
    val msg = APNSMessage(
      APSPayload(
        alert = Some(Right(AlertPayload("nice body", launchImage = Some("pic.jpg")))),
        badge = Some(5),
        sound = Some("rock.mp3")
      ),
      Map(
        "extra" -> toJson("value"),
        "number" -> Json.fromInt(5),
        "kings" -> toJson(Seq("hey", "you"))
      )
    )
    val str = stringify(toJson(msg))
    assert(str contains "launch-image")

    val msg2 = APNSMessage(
      APSPayload(alert = None, badge = Some(5), sound = Some("rock.mp3")),
      Map(
        "extra" -> toJson("value"),
        "number" -> Json.fromInt(5),
        "kings" -> toJson(Seq("hey", "you"))
      )
    )
    val str2 = stringify(toJson(msg2))
    assert(str2 contains APSPayload.ContentAvailable)
  }

  test("APS payload serializes correctly") {
    val payload = APSPayload(
      alert = Some(Right(AlertPayload("nice body", launchImage = Some("pic.jpg")))),
      badge = Some(5),
      sound = Some("rock.mp3")
    )
    val p1 = toJson(payload)
    assert(!(stringify(p1) contains APSPayload.ContentAvailable))
    val payload2 = APSPayload(None, Some(42), None)
    val p2 = toJson(payload2)
    assert(stringify(p2) contains APSPayload.ContentAvailable)
  }

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

  private def toJson[D: Encoder](d: D): Json = d.asJson
  private def stringify(json: Json): String = json.noSpaces
}

case class MyData(age: Int, name: String)

object MyData {
  implicit val format: Codec[MyData] = deriveCodec[MyData]
}
