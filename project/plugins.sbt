scalaVersion := "2.12.17"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.16",
  "org.scalameta" % "sbt-mdoc" % "2.3.7",
  "org.scalameta" % "sbt-scalafmt" % "2.5.0"
) map addSbtPlugin
