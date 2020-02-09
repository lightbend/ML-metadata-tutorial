import sbt._
import sbt.Keys._
import Dependencies._

name := "ML Learning tutorial"

version in ThisBuild := "0.1"
scalaVersion in ThisBuild := "2.12.10"

scalacOptions in ThisBuild := Seq("-Xexperimental", "-unchecked", "-deprecation", "-feature")
javaOptions in ThisBuild := Seq("Xlint:unchecked")

lazy val atlasclient = (project in file("./atlasclient"))
  .settings(libraryDependencies ++= atlasDependencies)

lazy val tensorflowAkka =  (project in file("./tensorflowakka"))
  .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
  .settings(
    libraryDependencies ++= Seq(tensorFlow, tensorFlowProto, logback, scalaTest),
    name := "tensorflow-akka",
    organization := "com.lightbend.cloudflow",

    scalaVersion := "2.12.10",
    crossScalaVersions := Vector(scalaVersion.value),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.8",
      "-Xlog-reflective-calls",
      "-Xlint",
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-deprecation",
      "-feature",
      "-language:_",
      "-unchecked"
    ),

    runLocalConfigFile := Some("src/main/resources/local.conf"),

    scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
  )