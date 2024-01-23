import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings


ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"


lazy val microservice = Project("child-benefit-admin-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 10653,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
    pipelineStages := Seq(gzip),
    RoutesKeys.routesImport ++= Seq(
        "java.time.LocalDate",
        "uk.gov.hmrc.childbenefitadminfrontend.models._",
        "uk.gov.hmrc.play.bootstrap.binders.RedirectUrl"
    ),
    TwirlKeys.templateImports ++= Seq(
        "play.twirl.api.HtmlFormat",
        "play.twirl.api.HtmlFormat._",
        "uk.gov.hmrc.govukfrontend.views.html.components._",
        "uk.gov.hmrc.hmrcfrontend.views.html.components._",
        "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
        "uk.gov.hmrc.hmrcfrontend.views.config._",
        "uk.gov.hmrc.childbenefitadminfrontend.views.ViewUtils._",
        "uk.gov.hmrc.childbenefitadminfrontend.views._",
        "uk.gov.hmrc.childbenefitadminfrontend.models._",
        "uk.gov.hmrc.childbenefitadminfrontend.controllers.routes",
        "uk.gov.hmrc.govukfrontend.views.html.components.implicits._",
        "uk.gov.hmrc.hmrcfrontend.views.html.components.implicits._"
    )
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(inConfig(Test)(testSettings))
  .settings()


lazy val testSettings: Seq[Def.Setting[_]] = Seq(
    unmanagedResourceDirectories += baseDirectory.value / "test" / "resources"
)

lazy val itTestSettings: Seq[Def.Setting[_]] = Seq(
    unmanagedResourceDirectories += baseDirectory.value / "it" / "resources"
)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings()
  )