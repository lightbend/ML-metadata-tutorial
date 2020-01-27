import Versions._
import sbt._

object Dependencies {

  val clientCommon    = "org.apache.atlas"                % "atlas-client-common"       % atlasVersion
  val atlasCommon     = "org.apache.atlas"                % "atlas-common"              % atlasVersion
  val atlasClient     = "org.apache.atlas"                % "atlas-client-v2"           % atlasVersion
  val atlasInt        = "org.apache.atlas"                % "atlas-intg"                % atlasVersion
  val gson            = "com.google.code.gson"            % "gson"                      %  gsonVersion

  val atlasDependencies    = Seq(clientCommon, atlasCommon, atlasClient, atlasInt)
}
