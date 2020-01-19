scalaVersion := "2.12.10"

Seq(
  "com.malliina" % "sbt-utils-maven" % "0.15.7",
  "org.scalameta" % "sbt-mdoc" % "2.1.1",
  "ch.epfl.scala" % "sbt-bloop" % "1.3.4",
  "org.scalameta" % "sbt-scalafmt" % "2.3.0"
) map addSbtPlugin
