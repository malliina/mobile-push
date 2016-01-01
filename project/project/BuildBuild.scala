import sbt.Keys._
import sbt._

object BuildBuild extends Build {

  override lazy val settings = super.settings ++ sbtPlugins ++ Seq(
    resolvers += Resolver.url(
      "bintray-sbt-plugin-releases",
      url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)
  )

  def sbtPlugins = Seq(
    "com.malliina" %% "sbt-utils" % "0.3.0",
    "me.lessis" % "bintray-sbt" % "0.3.0"
  ) map addSbtPlugin

  override lazy val projects = Seq(root)

  lazy val root = Project("plugins", file("."))
}
