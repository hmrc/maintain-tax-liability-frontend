import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.15.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "6.2.0-play-28",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % "1.1.0",
    "uk.gov.hmrc"             %% "tax-year"                   % "3.1.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatest"               %% "scalatest"              % "3.2.15",
    "org.scalatestplus.play"      %% "scalatestplus-play"     % "5.1.0",
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28"% "1.1.0",
    "org.jsoup"                   %  "jsoup"                  % "1.15.4",
    "com.typesafe.play"           %% "play-test"              % PlayVersion.current,
    "org.mockito"                 %% "mockito-scala-scalatest"% "1.17.14",
    "org.scalacheck"              %% "scalacheck"             % "1.17.0",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "com.github.tomakehurst"      % "wiremock-standalone"     % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.vladsch.flexmark"        %  "flexmark-all"           % "0.64.0"
  ).map(_ % "it, test")

}
