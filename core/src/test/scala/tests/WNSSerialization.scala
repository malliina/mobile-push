package tests

import com.malliina.push.wns.{Selection, _}

import scala.xml.{Node, PrettyPrinter}

class WNSSerialization extends munit.FunSuite {
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

  test("example") {
    // https://msdn.microsoft.com/en-us/windows/uwp/controls-and-patterns/tiles-and-notifications-adaptive-interactive-toasts
    val toast = ToastElement(
      ToastVisual(
        Seq(
          ToastBinding(
            ToastTemplate.ToastGeneric,
            Seq(WnsText("Spicy Heaven"), WnsText("When do you plan to come in tomorrow?")),
            Seq(Image("A.png", Option(Placement.AppLogoOverride))),
            Nil,
            None,
            None,
            None,
            None,
            None,
            None
          )
        )
      ),
      Actions(
        Seq(
          Input(
            "time",
            InputType.Selection,
            Seq(
              Selection("1", "Breakfast"),
              Selection("2", "Lunch"),
              Selection("3", "Dinner")
            ),
            Option("2")
          )
        ),
        Seq(
          ActionElement("Reserve", "reserve", ActivationType.Background),
          ActionElement("Call Restaurant", "call", ActivationType.Background)
        )
      ),
      Option("developer-defined-string"),
      None,
      None,
      None
    )
    //println(format(toast.xml))
  }

  def format(node: Node) = printer.format(node)
}
