import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.4.0"
  

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30"   % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30"   % "12.22.0",
    "uk.gov.hmrc" %% "internal-auth-client-play-30" % "4.3.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30"       % bootstrapVersion ,
    "org.jsoup"   %  "jsoup"                        % "1.18.1"
  ).map(_ % Test)
}
