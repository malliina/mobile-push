import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

inThisBuild(
  Seq(
    organization := "com.malliina",
    scalaVersion := "3.3.1",
    crossScalaVersions := Seq(scalaVersion.value, "2.13.13"),
    releaseCrossBuild := true
  )
)

val mavenCentralSettings = Seq(
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  Test / fork := true,
  libraryDependencies ++= Seq(
    "org.scalameta" %% "munit" % "1.0.0" % Test
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

val okClientVersion = "3.6.0"

val mobilePush = Project("mobile-push", file("core"))
  .enablePlugins(MavenCentralPlugin)
  .settings(mavenCentralSettings *)
  .settings(
    libraryDependencies ++= Seq("server", "client").map { m =>
      "org.eclipse.jetty" % s"jetty-alpn-java-$m" % "12.0.8"
    } ++ Seq(
      "com.malliina" %% "okclient" % okClientVersion,
      "com.nimbusds" % "nimbus-jose-jwt" % "9.37.3",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0"
    )
  )

val io = Project("mobile-push-io", file("io"))
  .enablePlugins(MavenCentralPlugin)
  .settings(mavenCentralSettings *)
  .dependsOn(mobilePush, mobilePush % "test->test")
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "okclient-io" % okClientVersion
    )
  )

val docs = project
  .in(file("mdoc"))
  .settings(
    publish / skip := true,
    mdocVariables := Map("VERSION" -> version.value),
    mdocOut := (ThisBuild / baseDirectory).value,
    updateDocs := {
      val log = streams.value.log
      val outFile = mdocOut.value
      IO.relativize((ThisBuild / baseDirectory).value, outFile)
        .getOrElse(sys.error(s"Strange directory: $outFile"))
      val addStatus = Process(s"git add $outFile").run(log).exitValue()
      if (addStatus != 0) {
        sys.error(s"Unexpected status code $addStatus for git commit.")
      }
    },
    updateDocs := updateDocs.dependsOn(mdoc.toTask("")).value
  )
  .dependsOn(mobilePush)
  .enablePlugins(MdocPlugin)

val mobilePushRoot = project
  .in(file("."))
  .enablePlugins(MavenCentralPlugin)
  .aggregate(mobilePush, io, docs)
  .settings(mavenCentralSettings *)
  .settings(
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    publish / skip := true,
    publishArtifact := false,
    packagedArtifacts := Map.empty,
    publish := {},
    publishLocal := {},
    releaseProcess := tagReleaseProcess.value,
    beforeCommitRelease := (docs / updateDocs).value
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
