scalaVersion := "2.12.13"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.1.0",
  "org.scalameta" % "sbt-mdoc" % "2.2.19",
  "ch.epfl.scala" % "sbt-bloop" % "1.4.8",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
