import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.15.0"
  

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "7.7.0-play-28",
    "uk.gov.hmrc" %% "internal-auth-client-play-28" % "1.2.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion            % "test, it",
    "org.mockito"             %% "mockito-scala"              % "1.16.42"                   % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"                    % Test,
  )
}
