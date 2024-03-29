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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
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
import uk.gov.hmrc.childbenefitadminfrontend.models.{ListResult, SubmissionItemStatus, SubmissionSummary}
import uk.gov.hmrc.childbenefitadminfrontend.views.html.SubmissionsView
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{FrontendAuthComponentsStub, StubBehaviour}

import java.time.{Instant, LocalDate}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SupplementaryDataSubmissionsControllerSpec
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
    .configure(
      "submissions.limit" -> 50
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

  "onPageLoad" - {

    val listResult = ListResult(
      totalCount = 2,
      List(
        SubmissionSummary("id1", "Submitted", None, Instant.now),
        SubmissionSummary("id2", "Processed", None, Instant.now)
      )
    )

    "must return OK and the correct view when the user is authorised and there are some supplementary data submissions" in {

      val request =
        FakeRequest(GET, routes.SupplementaryDataSubmissionsController.onPageLoad().url)
          .withSession("authToken" -> "Token some-token")
          .addAttr(RequestAttrKey.CSPNonce, "NONCE")

      val predicate = Permission(Resource(ResourceType("child-benefit-admin"), ResourceLocation("supplementary-data")), IAAction("ADMIN"))
      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.unit)
      when(mockSupplementaryDataConnector.list(any(), any(), any(), any())(any())).thenReturn(Future.successful(listResult))

      val result = route(app, request).value
      val view = app.injector.instanceOf[SubmissionsView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(listResult.summaries, None, None, 50, 0, listResult.totalCount)(request, implicitly).toString

      verify(mockStubBehaviour).stubAuth(Some(predicate), Retrieval.EmptyRetrieval)
    }

    "must use the parameters from the url when calling the supplementary data connector" in {

      val itemStatus = SubmissionItemStatus.Completed
      val created = LocalDate.of(2022, 2, 1)

      val url = routes.SupplementaryDataSubmissionsController.onPageLoad(
        status = Some(itemStatus),
        created = Some(created),
        offset = Some(5)
      ).url

      val request =
        FakeRequest(GET, url)
          .withSession("authToken" -> "Token some-token")
          .addAttr(RequestAttrKey.CSPNonce, "NONCE")

      when(mockStubBehaviour.stubAuth[Unit](any(), any())).thenReturn(Future.unit)
      when(mockSupplementaryDataConnector.list(any(), any(), any(), any())(any())).thenReturn(Future.successful(listResult))

      val result = route(app, request).value
      val view = app.injector.instanceOf[SubmissionsView]

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(listResult.summaries, Some(itemStatus), Some(created), 50, 5, listResult.totalCount)(request, implicitly).toString
      verify(mockSupplementaryDataConnector).list(eqTo(Some(itemStatus)), eqTo(Some(created)), eqTo(Some(50)), eqTo(Some(5)))(any())
    }

    "must redirect to login when the user is not authenticated" in {

      val request = FakeRequest(GET, routes.SupplementaryDataSubmissionsController.onPageLoad().url) // No authToken in session

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual s"/internal-auth-frontend/sign-in?continue_url=%2Fchild-benefit-admin%2Fsupplementary-data%2Fsubmissions"
    }

    "must fail when the user is not authorised" in {

      when(mockStubBehaviour.stubAuth(any(), any())).thenReturn(Future.failed(new Exception("foo")))

      val request =
        FakeRequest(GET, routes.SupplementaryDataSubmissionsController.onPageLoad().url)
          .withSession("authToken" -> "Token some-token")

      route(app, request).value.failed.futureValue
    }
  }
}
