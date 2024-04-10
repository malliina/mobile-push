scalaVersion := "2.12.19"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.35",
  "org.scalameta" % "sbt-mdoc" % "2.5.2",
  "org.scalameta" % "sbt-scalafmt" % "2.5.2"
) map addSbtPlugin
