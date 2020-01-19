scalaVersion := "2.12.10"

resolvers ++= Seq(
  ivyResolver(
    "bintray-sbt-plugin-releases",
    "https://dl.bintray.com/content/sbt/sbt-plugin-releases"
  ),
  ivyResolver("malliina bintray sbt", "https://dl.bintray.com/malliina/sbt-plugins/")
)

Seq(
  "com.malliina" % "sbt-utils-maven" % "0.15.2",
  "org.scalameta" % "sbt-mdoc" % "1.3.1",
  "ch.epfl.scala" % "sbt-bloop" % "1.3.4",
  "org.scalameta" % "sbt-scalafmt" % "2.3.0"
) map addSbtPlugin

def ivyResolver(name: String, urlStr: String) =
  Resolver.url(name, url(urlStr))(Resolver.ivyStylePatterns)
