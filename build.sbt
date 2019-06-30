import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

val commonSettings = Seq(
  scalaVersion := "2.13.0",
  organization := "com.malliina",
  crossScalaVersions := scalaVersion.value :: "2.12.8" :: Nil,
  releaseCrossBuild := true
)

val mobileSettings = commonSettings ++ Seq(
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  libraryDependencies ++= Seq(
    "com.malliina" %% "okclient" % "1.11.0",
    "com.nimbusds" % "nimbus-jose-jwt" % "7.3",
    "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
    "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
    "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.12.v20180117" % "runtime",
    "org.scalatest" %% "scalatest" % "3.0.8" % Test
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
  .settings(
    releaseProcess := tagReleaseProcess.value
  )

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
      val outFile = mdocOut.value
      IO.relativize((baseDirectory in ThisBuild).value, outFile)
        .getOrElse(sys.error(s"Strange directory: $outFile"))
      val addStatus = Process(s"git add $outFile").run(log).exitValue()
      if (addStatus != 0) {
        sys.error(s"Unexpected status code $addStatus for git commit.")
      }
    },
    updateDocs := updateDocs.dependsOn(mdoc.toTask("")).value
  )

beforePublish in mobileProject := (updateDocs in docs).value
