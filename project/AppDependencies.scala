import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % "5.2.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "0.60.0-play-28",
    "org.reactivemongo"       %% "play2-reactivemongo"        % "0.20.13-play27",
    "uk.gov.hmrc"             %% "tax-year"                   % "1.3.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"                % "3.0.8",
    "org.jsoup"               %  "jsoup"                    % "1.13.1",
    "com.typesafe.play"       %% "play-test"                % PlayVersion.current,
    "org.mockito"             %  "mockito-all"              % "1.10.19",
    "org.pegdown"             %  "pegdown"                  % "1.6.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3",
    "org.scalacheck"          %% "scalacheck"               % "1.14.3",
    "wolfendale"              %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.github.tomakehurst"  %  "wiremock-standalone"      % "2.25.1"
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
