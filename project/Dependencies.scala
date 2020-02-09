import Versions._
import sbt._

object Dependencies {

  val clientCommon    = "org.apache.atlas"                % "atlas-client-common"       % atlasVersion
  val atlasCommon     = "org.apache.atlas"                % "atlas-common"              % atlasVersion
  val atlasClient     = "org.apache.atlas"                % "atlas-client-v2"           % atlasVersion
  val atlasInt        = "org.apache.atlas"                % "atlas-intg"                % atlasVersion
  val gson            = "com.google.code.gson"            % "gson"                      %  gsonVersion

  val tensorFlow      = "org.tensorflow"                  % "tensorflow"                % tensorflowVersion
  val tensorFlowProto = "org.tensorflow"                  % "proto"                     % tensorflowVersion
  val logback         = "ch.qos.logback"                  % "logback-classic"           % logbackVersion

  val scalaTest       = "org.scalatest"                   %% "scalatest"                % scaltestVersion    % "test"

  val atlasDependencies    = Seq(clientCommon, atlasCommon, atlasClient, atlasInt)
}
