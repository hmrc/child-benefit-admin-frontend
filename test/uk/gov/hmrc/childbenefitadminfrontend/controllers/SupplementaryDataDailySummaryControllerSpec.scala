/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childbenefitadminfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.mockito.Mockito
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.request.RequestAttrKey
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.childbenefitadminfrontend.connectors.SupplementaryDataConnector
import uk.gov.hmrc.childbenefitadminfrontend.models.{DailySummary, DailySummaryResponse}
import uk.gov.hmrc.childbenefitadminfrontend.views.html.DailySummariesView
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{FrontendAuthComponentsStub, StubBehaviour}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SupplementaryDataDailySummaryControllerSpec
  extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  private val mockSupplementaryDataConnector = mock[SupplementaryDataConnector]
  private val mockStubBehaviour = mock[StubBehaviour]
  private val stubFrontendAuthComponents = FrontendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), implicitly)

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[FrontendAuthComponents].toInstance(stubFrontendAuthComponents),
      bind[SupplementaryDataConnector].toInstance(mockSupplementaryDataConnector)
    )
    .build()

  private implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  override protected def beforeEach(): Unit = {
    Mockito.reset(
      mockSupplementaryDataConnector,
      mockStubBehaviour
    )
    super.beforeEach()
  }

  /*
Analysis:
  [info]   "...nal-gtm-script-tag" [nonce="jpqd5xAlAGgwgvdNGqgWXQ==">(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start': new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0], j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src='https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','GTM-PLWQQZB');</script>
  [info] <!-- End Google Tag Manager -->
  [info]
  [info] <!--[if lte IE 8]>
  [info] <script src="/child-benefit-admin/hmrc-frontend/assets/vendor/html5shiv.min.js"></script>
  [info] <link href="/child-benefit-admin/hmrc-frontend/assets/hmrc-frontend-ie8-5.31.0.min.css" media="all" rel="stylesheet" type="text/css" />
  [info] <![endif]-->
  [info] <!--[if gt IE 8]><!-->
  [info] <link href="/child-benefit-admin/hmrc-frontend/assets/hmrc-frontend-5.31.0.min.css" nonce="jpqd5xAlAGgwgvdNGqgWXQ==" media="all" rel="stylesheet" type="text/css" />
  [info] <!--<![endif]-->
  [info]
  [info]
  [info]
  [info]
  [info]     <meta property="og:image" content="/child-benefit-admin/hmrc-frontend/assets/govuk/images/govuk-opengraph-image.png">
  [info]   </head>
  [info]   <body class="govuk-template__body ">
  [info]     <script nonce="jpqd5xAlAGgwgvdNGqgWXQ=="]>document.body.class..." ->

  [info]   "...nal-gtm-script-tag" [>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start': new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0], j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src='https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','GTM-PLWQQZB');</script>
  [info] <!-- End Google Tag Manager -->
  [info]
  [info] <!--[if lte IE 8]>
  [info] <script src="/child-benefit-admin/hmrc-frontend/assets/vendor/html5shiv.min.js"></script>
  [info] <link href="/child-benefit-admin/hmrc-frontend/assets/hmrc-frontend-ie8-5.31.0.min.css" media="all" rel="stylesheet" type="text/css" />
  [info] <![endif]-->
  [info] <!--[if gt IE 8]><!-->
  [info] <link href="/child-benefit-admin/hmrc-frontend/assets/hmrc-frontend-5.31.0.min.css"  media="all" rel="stylesheet" type="text/css" />
  [info] <!--<![endif]-->
  [info]
  [info]
  [info]
  [info]
  [info]     <meta property="og:image" content="/child-benefit-admin/hmrc-frontend/assets/govuk/images/govuk-opengraph-image.png">
  [info]   </head>
  [info]   <body class="govuk-template__body ">
  [info]     <script ]>document.body.class..."

              */

  "must return OK and the view when the server returns some data" in {

      val request =
        FakeRequest(GET, routes.SupplementaryDataDailySummaryController.onPageLoad().url)
          .withSession("authToken" -> "Token some-token")
          .addAttr(RequestAttrKey.CSPNonce, "NONCE")

      val dailySummaryResponse = DailySummaryResponse(
        summaries = List(DailySummary(LocalDate.now, 1, 2, 3, 4))
      )
      val predicate = Permission(Resource(ResourceType("child-benefit-admin"), ResourceLocation("supplementary-data")), IAAction("ADMIN"))
      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.unit)
      when(mockSupplementaryDataConnector.dailySummaries(any())).thenReturn(Future.successful(dailySummaryResponse))

      val result = route(app, request).value
      val view = app.injector.instanceOf[DailySummariesView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(dailySummaryResponse)(request, implicitly).toString

      verify(mockStubBehaviour).stubAuth(Some(predicate), Retrieval.EmptyRetrieval)
    }

    "must redirect to login when the user is not authenticated" in {

      val request = FakeRequest(GET, routes.SupplementaryDataDailySummaryController.onPageLoad().url) // No authToken in session

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual s"/internal-auth-frontend/sign-in?continue_url=%2Fchild-benefit-admin%2Fsupplementary-data%2Fsummaries"
    }

    "must fail when the user is not authorised" in {

      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.failed(new Exception("foo")))

      val request =
        FakeRequest(GET, routes.SupplementaryDataDailySummaryController.onPageLoad().url)
          .withSession("authToken" -> "Token some-token")

      route(app, request).value.failed.futureValue
    }
}
