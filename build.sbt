val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "com.malliina"
)

val mobileSettings = Seq(
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  libraryDependencies ++= Seq(
    "com.malliina" %% "okclient" % "1.8.1",
    "com.nimbusds" % "nimbus-jose-jwt" % "6.8",
    "com.notnoop.apns" % "apns" % "1.0.0.Beta6",
    "org.scala-lang.modules" %% "scala-xml" % "1.1.1",
    "org.mortbay.jetty.alpn" % "alpn-boot" % "8.1.12.v20180117" % "runtime",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
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

val docs = project.in(file("mdoc"))
  .enablePlugins(MdocPlugin)
  .dependsOn(mobileProject)
  .settings(commonSettings: _*)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
