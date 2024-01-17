import play.sbt.PlayImport.*
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30" % "8.3.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-30"  % "1.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test,
    "org.scalatestplus"       %% "mockito-4-6"              % "3.2.15.0"                    % Test,
    "org.jsoup"               %  "jsoup"                      % "1.15.4"                    % Test
  )
}
