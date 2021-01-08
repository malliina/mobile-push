scalaVersion := "2.12.12"

Seq(
  "com.malliina" % "sbt-utils-maven" % "1.0.0",
  "org.scalameta" % "sbt-mdoc" % "2.2.0",
  "ch.epfl.scala" % "sbt-bloop" % "1.4.4",
  "org.scalameta" % "sbt-scalafmt" % "2.4.2",
  "org.xerial.sbt" % "sbt-sonatype" % "3.9.4",
  "com.jsuereth" % "sbt-pgp" % "2.0.1",
  "com.github.gseitz" % "sbt-release" % "1.0.13"
) map addSbtPlugin
