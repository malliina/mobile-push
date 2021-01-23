import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

inThisBuild(
  Seq(
    organization := "com.malliina"
  )
)

val mobilePush = Project("mobile-push", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(
    scalaVersion := "2.12.12",
    crossScalaVersions := "2.13.3" :: "2.12.12" :: Nil,
    releaseCrossBuild := true,
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    libraryDependencies ++= Seq(
      "com.malliina" %% "okclient" % "1.17.0",
      "com.nimbusds" % "nimbus-jose-jwt" % "8.17.1",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
      "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
      "org.eclipse.jetty" % "jetty-alpn-java-server" % "9.4.20.v20190813",
      "org.eclipse.jetty" % "jetty-alpn-java-client" % "9.4.20.v20190813",
      "org.eclipse.jetty" % "jetty-alpn-openjdk8-server" % "9.4.20.v20190813",
      "org.eclipse.jetty" % "jetty-alpn-openjdk8-client" % "9.4.20.v20190813",
      "org.scalameta" %% "munit" % "0.7.12" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    fork in Test := true,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

val docs = project
  .in(file("mdoc"))
  .settings(
    scalaVersion := "2.12.12",
    crossScalaVersions -= "2.13.3",
    skip.in(publish) := true,
    mdocVariables := Map("VERSION" -> version.value),
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
  .dependsOn(mobilePush)
  .enablePlugins(MdocPlugin)

beforeCommitRelease in mobilePush := (updateDocs in docs).value

Global / onChangedBuildSource := ReloadOnSourceChanges
