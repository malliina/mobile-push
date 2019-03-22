import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "com.malliina"
)

val mobileSettings = commonSettings ++ Seq(
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  libraryDependencies ++= Seq(
    "com.malliina" %% "okclient" % "1.9.0",
    "com.nimbusds" % "nimbus-jose-jwt" % "7.0.1",
    "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
    "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
    "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.12.v20180117" % "runtime",
    "org.scalatest" %% "scalatest" % "3.0.7" % Test
  ),
  fork in Test := true,
  javaOptions ++= {
    val attList = (managedClasspath in Runtime).value
    for {
      file <- attList.map(_.data)
      path = file.getAbsolutePath
      if path.contains("alpn-boot")
    } yield s"-Xbootclasspath/p:$path"
  }
)

val mobileProject = Project("mobile-push", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(mobileSettings: _*)

val docs = project
  .in(file("mdoc"))
  .enablePlugins(MdocPlugin)
  .dependsOn(mobileProject)
  .settings(commonSettings: _*)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    mdocOut := (baseDirectory in ThisBuild).value,
    updateDocs := {
      val log = streams.value.log
      val commitStatus = Process(Seq("git", "commit", "-m", "Update documentation")).run(log).exitValue()
      if (commitStatus != 0) {
        sys.error(s"Unexpected status code $commitStatus for git commit.")
      }
    },
    updateDocs := updateDocs.dependsOn(mdoc.toTask("")).value
  )

beforePublish in mobileProject := (updateDocs in docs).value
