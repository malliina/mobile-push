import sbt.Keys._
import sbt._

object BuildBuild {

  lazy val settings = sbtPlugins ++ Seq(
    resolvers ++= Seq(
      ivyResolver("bintray-sbt-plugin-releases", "http://dl.bintray.com/content/sbt/sbt-plugin-releases"),
      ivyResolver("malliina bintray sbt", "https://dl.bintray.com/malliina/sbt-plugins/")
    )
  )

  def ivyResolver(name: String, urlStr: String) =
    Resolver.url(name, url(urlStr))(Resolver.ivyStylePatterns)

  def sbtPlugins = Seq(
    "com.malliina" %% "sbt-utils" % "0.6.1"
  ) map addSbtPlugin
}
