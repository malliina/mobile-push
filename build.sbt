import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

val scala2_13 = "2.13.6"

inThisBuild(
  Seq(
    organization := "com.malliina",
    scalaVersion := "3.0.0",
    crossScalaVersions := Seq(scalaVersion.value, scala2_13)
  )
)

val jettyModules = Seq(
  "java-server",
  "java-client"
)

val mavenCentralSettings = Seq(
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  Test / fork := true,
  libraryDependencies ++= Seq(
    "org.scalameta" %% "munit" % "0.7.27" % Test
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

val okClientVersion = "2.0.0-SNAPSHOT"

val mobilePush = Project("mobile-push", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(mavenCentralSettings: _*)
  .settings(
    libraryDependencies ++= jettyModules.map { m =>
      "org.eclipse.jetty" % s"jetty-alpn-$m" % "9.4.40.v20210413"
    } ++ Seq(
      "com.malliina" %% "okclient" % okClientVersion,
      "com.nimbusds" % "nimbus-jose-jwt" % "9.10.1",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
      "org.scala-lang.modules" %% "scala-xml" % "2.0.0"
    )
  )

val io = Project("mobile-push-io", file("io"))
  .enablePlugins(MavenCentralPlugin)
  .settings(mavenCentralSettings: _*)
  .dependsOn(mobilePush)
  .settings(
    libraryDependencies ++= Seq(
      "com.malliina" %% "okclient-io" % okClientVersion
    )
  )

val docs = project
  .in(file("mdoc"))
  .settings(
    scalaVersion := scala2_13,
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

val root = project
  .in(file("solution"))
  .aggregate(mobilePush, io, docs)
  .settings(
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))),
    publish / skip := true,
    publishArtifact := false,
    packagedArtifacts := Map.empty,
    publish := {},
    publishLocal := {},
    releaseProcess := (mobilePush / tagReleaseProcess).value
  )

mobilePush / beforeCommitRelease := (docs / updateDocs).value

Global / onChangedBuildSource := ReloadOnSourceChanges
