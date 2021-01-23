scalaVersion := "2.12.12"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.0.0",
  "org.scalameta" % "sbt-mdoc" % "2.2.0",
  "ch.epfl.scala" % "sbt-bloop" % "1.4.4",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2"
) map addSbtPlugin
