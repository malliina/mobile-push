import bintray.Plugin.bintraySettings
import com.mle.sbtutils.{SbtProjects, SbtUtils}
import sbt.Keys._
import sbt._

/**
 * A scala build file template.
 */
object PushBuild extends Build {
  lazy val mobileProject = SbtProjects.mavenPublishProject("mobile-push").settings(projectSettings: _*)

  lazy val projectSettings = bintraySettings ++ Seq(
    SbtUtils.gitUserName := "malliina",
    SbtUtils.developerName := "Michael Skogberg",
    version := "0.9.3",
    scalaVersion := "2.11.6",
    fork in Test := true,
    resolvers := Seq(
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
      sbt.Resolver.jcenterRepo,
      "Bintray malliina" at "http://dl.bintray.com/malliina/maven") ++ resolvers.value,
    libraryDependencies ++= Seq(
      "com.github.malliina" %% "util" % "1.8.1",
      "com.typesafe.play" %% "play-json" % "2.3.8",
      "com.ning" % "async-http-client" % "1.8.13",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6"),
    libraryDependencies := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-xml" % "1.0.1"
        case _ =>
          libraryDependencies.value
      }
    },
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )
}
