import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

lazy val mobileProject = SbtProjects.mavenPublishProject("mobile-push")

scalaVersion := "2.12.3"

crossScalaVersions := Seq("2.11.11", scalaVersion.value)
releaseCrossBuild := true
gitUserName := "malliina"
organization := "com.malliina"
developerName := "Michael Skogberg"
resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
libraryDependencies ++= Seq(
  "com.malliina" %% "util" % "2.8.1",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
  "com.squareup.okhttp" % "okhttp" % "2.7.5",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.6.v20151105" % "runtime"
)
fork in Test := true
javaOptions ++= {
  val attList = (managedClasspath in Runtime).value
  for {
    file <- attList.map(_.data)
    path = file.getAbsolutePath
    if path.contains("jetty.alpn")
  } yield "-Xbootclasspath/p:" + path
}
