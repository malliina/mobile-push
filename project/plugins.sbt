scalaVersion := "2.12.19"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.38",
  "org.scalameta" % "sbt-mdoc" % "2.5.3",
  "org.scalameta" % "sbt-scalafmt" % "2.5.2"
) map addSbtPlugin
