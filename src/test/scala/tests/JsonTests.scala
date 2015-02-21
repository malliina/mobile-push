package tests

import com.mle.push.apns.{APNSMessage, APSPayload, AlertPayload}
import org.scalatest.FunSuite
import play.api.libs.json.Json._
import play.api.libs.json.{JsNumber, JsObject, Json}

/**
 * @author Michael
 */
class JsonTests extends FunSuite {
  test("can json this") {
    val obj = Json.toJson(Map("a" -> "b")).as[JsObject]
    assert((obj \ "a").as[String] === "b")
  }
  test("APNS serialization") {
    val msg = APNSMessage(APSPayload(
      alert = Right(AlertPayload("nice body", launchImage = Some("pic.jpg"))),
      badge = Some(5),
      sound = Some("rock.mp3")),
      Map("extra" -> toJson("value"), "number" -> JsNumber(5), "kings" -> toJson(Seq("hey", "you"))))
    val str = prettyPrint(toJson(msg))
//    println(str)
  }
}
