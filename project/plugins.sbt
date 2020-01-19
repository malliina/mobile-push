scalaVersion := "2.12.10"

Seq(
  "com.malliina" %% "sbt-utils-maven" % "0.15.5",
  "org.scalameta" % "sbt-mdoc" % "1.3.1",
  "ch.epfl.scala" % "sbt-bloop" % "1.3.4",
  "org.scalameta" % "sbt-scalafmt" % "2.3.0"
) map addSbtPlugin

def ivyResolver(name: String, urlStr: String) =
  Resolver.url(name, url(urlStr))(Resolver.ivyStylePatterns)
