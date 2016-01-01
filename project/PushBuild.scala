import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

/**
  * A scala build file template.
  */
object PushBuild extends Build {
  lazy val mobileProject = SbtProjects.testableProject("mobile-push")
    .enablePlugins(bintray.BintrayPlugin)
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    version := "1.3.1",
    scalaVersion := "2.11.7",
    gitUserName := "malliina",
    organization := s"com.${gitUserName.value}",
    developerName := "Michael Skogberg",
    fork in Test := true,
    resolvers := Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("malliina", "maven")) ++ resolvers.value,
    libraryDependencies ++= Seq(
      "com.malliina" %% "util" % "2.1.0",
      "com.typesafe.play" %% "play-json" % "2.4.6",
      "com.ning" % "async-http-client" % "1.9.31",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6"
    ),
    libraryDependencies := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor >= 11 =>
          libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
        case _ =>
          libraryDependencies.value
      }
    },
    licenses +=("MIT", url("http://opensource.org/licenses/MIT"))
  )
}
