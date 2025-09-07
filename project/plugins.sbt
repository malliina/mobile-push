scalaVersion := "2.12.20"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.57",
  "org.scalameta" % "sbt-mdoc" % "2.7.2",
  "org.scalameta" % "sbt-scalafmt" % "2.5.5"
) map addSbtPlugin
