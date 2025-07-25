import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.18.0"
  val mongoVersion = "2.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30" % "9.11.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"             %% "tax-year"                   % "5.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.18.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
