import sbtrelease.ReleaseStateTransformations._

import scala.sys.process.Process

val updateDocs = taskKey[File]("Updates README.md")

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
    "org.scalatest" %% "scalatest" % "3.0.6" % Test
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
    updateDocs := {
      val log = streams.value.log
      val outFile = (baseDirectory in ThisBuild).value / "README.md"
      IO.move(mdocOut.value / "README.md", outFile)
      log.info(s"Wrote README to $outFile. Committing...")
      val addStatus = Process(s"git add $outFile").run(log).exitValue()
      val commitStatus =  Process(Seq("git", "commit", "-m", "Update README")).run(log).exitValue()
      if (addStatus != 0 || commitStatus != 0) {
        sys.error(s"Unexpected status codes $addStatus, $commitStatus for git add/commit.")
      }
      outFile
    },
    updateDocs := updateDocs.dependsOn(mdoc.toTask("")).value
  )

releaseProcess in mobileProject := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  releaseStepTask(updateDocs in docs),
  commitReleaseVersion,
  tagRelease,
  publishArtifacts, // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
