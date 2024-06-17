import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / majorVersion := 0

lazy val microservice = Project("maintain-tax-liability-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies(),
    scalacOptions ++= Seq("-feature", "-Wconf:src=routes/.*:s", "-Wconf:cat=unused-imports&src=html/.*:s"),
    RoutesKeys.routesImport += "models._",
    RoutesKeys.routesImport += "models.CYMinusNTaxYears._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "controllers.routes._",
      "views.ViewUtils._"
    ),
    PlayKeys.playDefaultPort := 9844,
    ScoverageKeys.coverageExcludedFiles := "<empty>;.*components.*;.*Mode.*;.*Routes.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Assets / pipelineStages := Seq(gzip),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/maintaintaxliabilityfrontend-app.js" ->
        group(Seq(
          "javascripts/maintaintaxliabilityfrontend.js",
          "javascripts/iebacklink.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat,uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("maintaintaxliabilityfrontend-*.js")
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())


addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle it/Test/scalastyle")
