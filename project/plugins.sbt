scalaVersion := "2.12.18"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.29",
  "org.scalameta" % "sbt-mdoc" % "2.5.1",
  "org.scalameta" % "sbt-scalafmt" % "2.5.2"
) map addSbtPlugin
