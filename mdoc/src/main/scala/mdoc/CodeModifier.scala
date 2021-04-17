package mdoc

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import scala.jdk.CollectionConverters.CollectionHasAsScala

/** Borrows the syntax from https://github.com/playframework/play-doc.
  *
  * To insert a code block, first wrap the desired code with a comment '//#label-name-here',
  * then insert the block in your markdown file with
  *
  * {{{
  *   ```scala mdoc:code:code/CodeSamples.scala:example1
  *   42
  *   ```
  * }}}
  *
  */
class CodeModifier extends PreModifier {
  override val name = "code"

  override def process(ctx: PreModifierContext): String = {
    // Given a markdown of ```scala mdoc:code:file:label, ctx.info is "file:label"
    ctx.info.split(":", 2).toList match {
      case file :: label :: Nil =>
        val sourceFile = ctx.inputFile.toNIO.getParent.resolve(file)
        val lines = Files.readAllLines(sourceFile, StandardCharsets.UTF_8).asScala.toList
        val delimiter = s"//#$label"
        val start = lines.indexWhere(_.trim == delimiter)
        val end = lines.indexWhere(_.trim == delimiter, start + 1)
        if (start >= 0 || end >= 0) {
          val slice = lines.slice(start + 1, end)
          val indentCount = indents(slice)
          (Seq("```scala") ++ slice.map(_.drop(indentCount)) ++ Seq("```")).mkString("\n")
        } else {
          ctx.reporter.error(s"Start/end labels $label not found in $file.")
          ""
        }
      case _ =>
        ctx.reporter.error(s"File and label not found. Use format 'file:label'.")
        ""
    }
  }

  def indents(lines: Seq[String]) =
    lines.filter(_.trim.nonEmpty).map(_.takeWhile(_ == ' ').length).min
}
