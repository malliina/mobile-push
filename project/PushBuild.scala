import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

object PushBuild {
  lazy val mobileProject = SbtProjects.mavenPublishProject("mobile-push")
    .settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    scalaVersion := "2.11.8",
    gitUserName := "malliina",
    organization := "com.malliina",
    developerName := "Michael Skogberg",
    libraryDependencies ++= Seq(
      "com.malliina" %% "util" % "2.5.0",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
      "com.squareup.okhttp" % "okhttp" % "2.7.5",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    ),
    libraryDependencies += "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.6.v20151105" % "runtime",
    fork in Test := true,
    javaOptions ++= {
      val attList = (managedClasspath in Runtime).value
      for {
        file <- attList.map(_.data)
        path = file.getAbsolutePath
        if path.contains("jetty.alpn")
      } yield "-Xbootclasspath/p:" + path
    }
  )
}
