import Dependencies._

name := "ML Learning tutorial"

version in ThisBuild := "0.1"
scalaVersion in ThisBuild := "2.12.8"

scalacOptions in ThisBuild := Seq("-Xexperimental", "-unchecked", "-deprecation", "-feature")
javaOptions in ThisBuild := Seq("Xlint:unchecked")

lazy val atlasclient = (project in file("./atlasclient"))
  .settings(libraryDependencies ++= atlasDependencies)
