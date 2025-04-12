scalaVersion := "2.12.20"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.47",
  "org.scalameta" % "sbt-mdoc" % "2.6.4",
  "org.scalameta" % "sbt-scalafmt" % "2.5.4"
) map addSbtPlugin
