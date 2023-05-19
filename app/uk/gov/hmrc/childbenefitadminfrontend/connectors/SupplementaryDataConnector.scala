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

package uk.gov.hmrc.childbenefitadminfrontend.connectors

import play.api.Configuration
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.childbenefitadminfrontend.config.Service
import uk.gov.hmrc.childbenefitadminfrontend.models._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.net.URL
import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplementaryDataConnector @Inject()(
                                        httpClient: HttpClientV2,
                                        configuration: Configuration
                                      )(implicit ec: ExecutionContext) {

  private val claimChildBenefitService: Service = configuration.get[Service]("microservice.services.child-benefit-service")

  def get(id: String)(implicit hc: HeaderCarrier): Future[Option[SubmissionItem]] =
    httpClient
      .get(url"${claimChildBenefitService.baseUrl}/child-benefit-service/supplementary-data/$id")
      .execute[Option[SubmissionItem]]

  def list(
            status: Option[SubmissionItemStatus] = None,
            created: Option[LocalDate] = None,
            limit: Option[Int] = None,
            offset: Option[Int] = None
          )(implicit hc: HeaderCarrier): Future[ListResult] = {

    val localDateBinder: QueryStringBindable[LocalDate] = implicitly
    val statusBinder: QueryStringBindable[SubmissionItemStatus] = implicitly
    val intBinder: QueryStringBindable[Int] = implicitly

    val params = List(
      status.map(statusBinder.unbind("status", _)),
      created.map(localDateBinder.unbind("created", _)),
      limit.map(intBinder.unbind("limit", _)),
      offset.map(intBinder.unbind("offset", _))
    ).flatten

    val query = if (params.isEmpty) "" else {
      params.mkString("?", "&", "")
    }

    httpClient
      .get(new URL(s"${claimChildBenefitService.baseUrl}/child-benefit-service/supplementary-data$query"))
      .execute[ListResult]
  }

  def dailySummaries(implicit hc: HeaderCarrier): Future[DailySummaryResponse] =
    httpClient
      .get(url"${claimChildBenefitService.baseUrl}/child-benefit-service/supplementary-data/summaries")
      .execute[DailySummaryResponse]
}