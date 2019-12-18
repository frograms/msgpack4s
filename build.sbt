import ReleaseTransformations._

name := "msgpack4s"

val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "org.velvia",
  crossScalaVersions := Seq("2.13.1", "2.12.8")
)

unmanagedSourceDirectories in Compile <++= Seq(baseDirectory(_ / "src" )).join

unmanagedSourceDirectories in Test <++= Seq(baseDirectory(_ / "test" )).join

// Testing deps
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.1.0" % "test",
                            "org.mockito" % "mockito-all" % "1.9.0" % "test")

lazy val json4s     = "org.json4s" %% "json4s-native" % "3.6.7"
lazy val commonsIo  = "org.apache.commons" % "commons-io" % "1.3.2"

// Extra dependencies for type classes for JSON libraries
libraryDependencies ++= Seq(json4s     % "provided")

licenses += ("Apache-2.0", url("http://choosealicense.com/licenses/apache/"))

// POM settings for Sonatype
homepage := Some(url("https://github.com/velvia/msgpack4s"))

scmInfo := Some(ScmInfo(url("https://github.com/velvia/msgpack4s"),
                            "git@github.com:velvia/msgpack4s.git"))

developers := List(Developer("velvia",
                        "Evan Chan",
                        "velvia@gmail.com",
                        url("https://github.com/velvia")))

pomIncludeRepository := (_ => false)

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)

lazy val msgpack4s = (project in file(".")).settings(commonSettings: _*)

lazy val jmh = (project in file("jmh")).dependsOn(msgpack4s)
                        .settings(commonSettings: _*)
                        .settings(jmhSettings: _*)
                        .settings(libraryDependencies += json4s)
                        .settings(libraryDependencies += commonsIo)
