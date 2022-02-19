package com.malliina.push

import java.nio.file.{Path, Paths}
import scala.io.{BufferedSource, Source}

object PushUtils extends PushUtils

trait PushUtils {
  def userHome = Paths.get(sys.props("user.home"))

  def props(path: Path) = {
    val src = Source.fromFile(path.toUri)
    try mappify(src)
    finally src.close()
  }

  private def mappify(src: BufferedSource) = src
    .getLines()
    .filter(line => line.contains("=") && !line.startsWith("#") && !line.startsWith("//"))
    .map(line => line.split("=", 2))
    .filter(_.length >= 2)
    .map(arr => arr(0) -> arr(1))
    .toMap
}
