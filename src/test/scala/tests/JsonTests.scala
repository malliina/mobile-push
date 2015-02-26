package tests

import com.mle.push.apns.{APNSMessage, APSPayload, AlertPayload}
import org.scalatest.FunSuite
import play.api.libs.json.Json._
import play.api.libs.json._

import scala.util.Success

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
      alert = Some(Right(AlertPayload("nice body", launchImage = Some("pic.jpg")))),
      badge = Some(5),
      sound = Some("rock.mp3")),
      Map("extra" -> toJson("value"), "number" -> JsNumber(5), "kings" -> toJson(Seq("hey", "you"))))
    val str = prettyPrint(toJson(msg))
    println(str)

    val msg2 = APNSMessage(APSPayload(
      alert = None,
      badge = Some(5),
      sound = Some("rock.mp3")),
      Map("extra" -> toJson("value"), "number" -> JsNumber(5), "kings" -> toJson(Seq("hey", "you"))))
    val str2 = prettyPrint(toJson(msg2))
    println(str2)
  }
}
case class MyData(age: Int, name: String)
object MyData {
  implicit val format = Json.format[MyData]
}
