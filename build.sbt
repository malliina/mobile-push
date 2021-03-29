import scala.sys.process.Process

val updateDocs = taskKey[Unit]("Updates README.md")

inThisBuild(
  Seq(
    organization := "com.malliina",
    scalaVersion := "2.13.5"
  )
)

val jettyModules = Seq(
  "java-server",
  "java-client",
  "openjdk8-server",
  "openjdk8-client"
)

val mobilePush = Project("mobile-push", file("."))
  .enablePlugins(MavenCentralPlugin)
  .settings(
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    libraryDependencies ++= jettyModules.map { m =>
      "org.eclipse.jetty" % s"jetty-alpn-$m" % "9.4.20.v20190813",
    } ++ Seq(
      "com.malliina" %% "okclient" % "1.18.1",
      "com.nimbusds" % "nimbus-jose-jwt" % "9.7",
      "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
      "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
      "org.scalameta" %% "munit" % "0.7.23" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    fork in Test := true,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

val docs = project
  .in(file("mdoc"))
  .settings(
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
