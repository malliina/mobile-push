scalaVersion := "2.12.13"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.2.4",
  "org.scalameta" % "sbt-mdoc" % "2.2.21",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
