scalaVersion := "2.12.20"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.6.43",
  "org.scalameta" % "sbt-mdoc" % "2.6.2",
  "org.scalameta" % "sbt-scalafmt" % "2.5.2"
) map addSbtPlugin
