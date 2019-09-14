scalaVersion := "2.12.8"

resolvers ++= Seq(
  ivyResolver("bintray-sbt-plugin-releases",
              "https://dl.bintray.com/content/sbt/sbt-plugin-releases"),
  ivyResolver("malliina bintray sbt",
              "https://dl.bintray.com/malliina/sbt-plugins/")
)

Seq(
  "com.malliina" %% "sbt-utils-maven" % "0.14.0",
  "org.scalameta" % "sbt-mdoc" % "1.3.1"
) map addSbtPlugin

def ivyResolver(name: String, urlStr: String) =
  Resolver.url(name, url(urlStr))(Resolver.ivyStylePatterns)
