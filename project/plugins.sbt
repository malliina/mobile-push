scalaVersion := "2.12.15"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.2.6",
  "org.scalameta" % "sbt-mdoc" % "2.2.24",
  "org.scalameta" % "sbt-scalafmt" % "2.4.6"
) map addSbtPlugin
