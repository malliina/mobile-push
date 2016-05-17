package tests

import com.malliina.push.wns._
import org.scalatest.FunSuite

import scala.xml.{Node, PrettyPrinter}

class WNSSerialization extends FunSuite {
  val printer = new PrettyPrinter(200, 2)

  test("audio") {
    val audio1 = Audio.once("britney.mp3").xml.toString()
    assert(audio1 contains """src="britney.mp3"""")
    val audio2 = Audio.Mute.xml
    assert(!(audio2 contains "src"))
  }

  test("seqs") {
    def elem(i: Int) =
      <elem>
        {i}
      </elem>
    val elems = Seq(elem(1), elem(2))
    val result =
      <result>
        {elems}
      </result>
  }

//  test("toast") {
//    val toast = ToastElement(None,
//      ActivationType.Foreground,
//      Scenario.IncomingCall,
//      Actions(Nil))
//    println(format(toast.xml))
//    val action1 = ActionElement("my content",
//      "arguments 1 2 3",
//      ActivationType.System,
//      Option("www.google.com/pic.png"),
//      "this is the hint")
//    val toast2 = ToastElement(None,
//      ActivationType.Background,
//      Scenario.Reminder,
//      Actions(Seq(action1)))
//    println(format(toast2.xml))
//  }

  def format(node: Node) = printer.format(node)
}
