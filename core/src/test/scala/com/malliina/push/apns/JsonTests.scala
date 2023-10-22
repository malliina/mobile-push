package com.malliina.push.apns

import com.malliina.push.apns.APSPayload.CriticalSound
import io.circe._
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax.EncoderOps

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
        sound = Some(Left("rock.mp3"))
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
      APSPayload(alert = None, badge = Some(5), sound = Some(Left("rock.mp3"))),
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
      sound = Some(Left("rock.mp3"))
    )
    val p1 = toJson(payload)
    assert(!(stringify(p1) contains APSPayload.ContentAvailable))
    val payload2 = APSPayload(None, Some(42), None)
    val p2 = toJson(payload2)
    assert(stringify(p2) contains APSPayload.ContentAvailable)
  }

  test("APS with critical sound") {
    val msg = APSPayload(None, sound = Option(Right(CriticalSound(1, "Hello", 7))))
    assertEquals(
      msg.asJson.noSpaces,
      """{"sound":{"critical":1,"name":"Hello","volume":7},"content-available":1}"""
    )
  }

  private def toJson[D: Encoder](d: D): Json = d.asJson
  private def stringify(json: Json): String = json.noSpaces
}

case class MyData(age: Int, name: String)

object MyData {
  implicit val format: Codec[MyData] = deriveCodec[MyData]
}
