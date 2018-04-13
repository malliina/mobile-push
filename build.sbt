import com.malliina.sbtutils.SbtProjects
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

lazy val mobileProject = SbtProjects.mavenPublishProject("mobile-push")

scalaVersion := "2.12.5"

gitUserName := "malliina"
organization := "com.malliina"
developerName := "Michael Skogberg"
resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
libraryDependencies ++= Seq(
  "com.malliina" %% "okclient" % "1.5.2",
  "com.nimbusds" % "nimbus-jose-jwt" % "5.9",
  "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.12.v20180117" % "runtime"
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
