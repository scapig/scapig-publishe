import _root_.play.sbt.PlayImport._

import sbt.Keys._
import sbt._

name := "scapig-publisher"

version := "1.0"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq( ws, guice )
libraryDependencies += "com.typesafe.play" %% "play-json-joda" % "2.6.0"
libraryDependencies += "org.raml" % "raml-parser-2" % "1.0.3"
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.12.0" % "test"
libraryDependencies += "com.github.tomakehurst" % "wiremock-standalone" % "2.8.0" % "it, component"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1" % "test"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0" % "it, component"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

lazy val microservice = (project in file("."))
  .enablePlugins(Seq(play.sbt.PlayScala) : _*)
  .enablePlugins(LauncherJarPlugin)
  .configs(IntTest)
  .settings(inConfig(IntTest)(Defaults.testSettings): _*)
  .settings(
    Keys.fork in IntTest := false,
    unmanagedSourceDirectories in IntTest <<= (baseDirectory in IntTest) (base => Seq(base / "it"))
  )
  .configs(ComponentTest)
  .settings(inConfig(ComponentTest)(Defaults.testSettings): _*)
  .settings(
    Keys.fork in ComponentTest := false,
    unmanagedSourceDirectories in ComponentTest <<= (baseDirectory in ComponentTest) (base => Seq(base / "component"))
  )

lazy val IntTest = config("it") extend Test
lazy val ComponentTest = config("component") extend Test
