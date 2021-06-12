/*
 * Copyright 2021 HM Revenue & Customs
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

package utils.print

import base.SpecBase
import controllers.routes
import models._
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class PrintHelperSpec extends SpecBase {

  val printHelper: PrintHelper = injector.instanceOf[PrintHelper]
  val taxYearRange: TaxYearRange = injector.instanceOf[TaxYearRange]

  "PrintHelper" must {

    "render answer rows" in {

      val userAnswers = emptyUserAnswers
        .set(CYMinusFourYesNoPage, true).success.value
        .set(DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYear), true).success.value

        .set(CYMinusThreeYesNoPage, true).success.value
        .set(DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYear), true).success.value

        .set(CYMinusTwoYesNoPage, true).success.value
        .set(DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value

        .set(CYMinusOneYesNoPage, true).success.value
        .set(DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

      val result = printHelper.answerRows(userAnswers)

      result mustBe Seq(
        AnswerSection(
          heading = messages("checkYourAnswersSection.heading", taxYearRange.taxYearDates(CYMinus4TaxYear): _*),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusFour.liability.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus4TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusFourYesNoController.onPageLoad().url
            ),
            AnswerRow(
              label = messages("declaredToHMRC.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus4TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus4TaxYear).url
            )
          )
        ),
        AnswerSection(
          heading = messages("checkYourAnswersSection.heading", taxYearRange.taxYearDates(CYMinus3TaxYear): _*),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusThree.liability.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus3TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusThreeYesNoController.onPageLoad().url
            ),
            AnswerRow(
              label = messages("declaredToHMRC.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus3TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus3TaxYear).url
            )
          )
        ),
        AnswerSection(
          heading = messages("checkYourAnswersSection.heading", taxYearRange.taxYearDates(CYMinus2TaxYear): _*),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusTwo.liability.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus2TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusTwoYesNoController.onPageLoad().url
            ),
            AnswerRow(
              label = messages("declaredToHMRC.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus2TaxYear): _*),
              answer = Html("No"),
              changeUrl = routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus2TaxYear).url)
          )
        ),
        AnswerSection(
          heading = messages("checkYourAnswersSection.heading", taxYearRange.taxYearDates(CYMinus1TaxYear): _*),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusOne.liability.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus1TaxYear): _*),
              answer = Html("Yes"),
              changeUrl = routes.CYMinusOneYesNoController.onPageLoad().url
            ),
            AnswerRow(
              label = messages("declaredToHMRC.checkYourAnswersLabel", taxYearRange.taxYearDates(CYMinus1TaxYear): _*),
              answer = Html("No"),
              changeUrl = routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus1TaxYear).url
            )
          )
        )
      )
    }
  }

}
