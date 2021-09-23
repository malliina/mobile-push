scalaVersion := "2.12.15"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.2.5",
  "org.scalameta" % "sbt-mdoc" % "2.2.23",
  "org.scalameta" % "sbt-scalafmt" % "2.4.3"
) map addSbtPlugin
