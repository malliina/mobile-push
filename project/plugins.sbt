scalaVersion := "2.12.4"

resolvers ++= Seq(
  ivyResolver("bintray-sbt-plugin-releases", "http://dl.bintray.com/content/sbt/sbt-plugin-releases"),
  ivyResolver("malliina bintray sbt", "https://dl.bintray.com/malliina/sbt-plugins/")
)

addSbtPlugin("com.malliina" %% "sbt-utils" % "0.8.0")

def ivyResolver(name: String, urlStr: String) =
  Resolver.url(name, url(urlStr))(Resolver.ivyStylePatterns)
