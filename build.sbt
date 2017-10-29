import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

lazy val mobileProject = SbtProjects.mavenPublishProject("mobile-push")

scalaVersion := "2.12.4"

gitUserName := "malliina"
organization := "com.malliina"
developerName := "Michael Skogberg"
resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
libraryDependencies ++= Seq(
  "com.malliina" %% "util" % "2.8.2",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
  "com.squareup.okhttp3" % "okhttp" % "3.9.0",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.11.v20170118" % "runtime"
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
