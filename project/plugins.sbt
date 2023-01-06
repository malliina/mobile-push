scalaVersion := "2.12.17"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.4.0",
  "org.scalameta" % "sbt-mdoc" % "2.3.6",
  "org.scalameta" % "sbt-scalafmt" % "2.5.0"
) map addSbtPlugin
