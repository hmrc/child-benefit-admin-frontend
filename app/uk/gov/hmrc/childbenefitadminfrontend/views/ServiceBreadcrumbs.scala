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

package uk.gov.hmrc.childbenefitadminfrontend.views

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{BreadcrumbsItem, Text}
import uk.gov.hmrc.childbenefitadminfrontend.controllers
import uk.gov.hmrc.childbenefitadminfrontend.models._

object ServiceBreadcrumbs {

  def index(implicit messages: Messages): BreadcrumbsItem = BreadcrumbsItem(
    content = Text(messages("breadcrumbs.index")),
    href = Some(controllers.routes.IndexController.onPageLoad().url)
  )

  def supplementaryDataDailySummary(implicit messages: Messages): BreadcrumbsItem = BreadcrumbsItem(
    content = Text(messages("breadcrumbs.supplementaryDataSummaries")),
    href = Some(controllers.routes.SupplementaryDataDailySummaryController.onPageLoad().url)
  )

  def supplementaryDataSubmissions(submission: SubmissionItem)(implicit messages: Messages): BreadcrumbsItem = BreadcrumbsItem(
    content = Text(messages("breadcrumbs.supplementaryDataSubmissions", submission.status)),
    href = Some(controllers.routes.SupplementaryDataSubmissionsController.onPageLoad(status = Some(submission.status)).url)
  )
}
