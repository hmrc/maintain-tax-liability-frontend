import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.24.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "1.8.0-play-28",
    "org.reactivemongo"       %% "play2-reactivemongo"        % "0.20.13-play27",
    "uk.gov.hmrc"             %% "tax-year"                   % "1.3.0",
    "uk.gov.hmrc"             %% "play-language"              % "5.1.0-play-27"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"              % "3.2.12",
    "org.scalatestplus.play"      %% "scalatestplus-play"     % "5.1.0",
    "org.jsoup"                   %  "jsoup"                  % "1.15.1",
    "com.typesafe.play"           %% "play-test"              % PlayVersion.current,
    "org.mockito"                 %  "mockito-all"            % "1.10.19",
    "org.mockito"                 %  "mockito-core"           % "4.6.1",
    "org.scalacheck"              %% "scalacheck"             % "1.16.0",
    "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.scalatestplus"         %% "mockito-3-12" % "3.2.10.0",
    "com.github.tomakehurst"      % "wiremock-standalone"     % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"  % "0.1.2",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.62.2"
  ).map(_ % "it, test")

  private val akkaVersion = "2.6.12"
  private val akkaHttpVersion = "10.2.3"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion,
    "commons-codec" % "commons-codec" % "1.12"
  )

}
