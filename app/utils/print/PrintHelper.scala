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

import models._
import pages._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class PrintHelper @Inject()(taxYearRange: TaxYearRange,
                            answerRowConverter: AnswerRowConverter) {

  def answerRows(userAnswers: UserAnswers)(implicit messages: Messages): Seq[AnswerSection] = {

    val bound = answerRowConverter.bind(userAnswers)
    
    TaxYear.taxYears.foldLeft[Seq[AnswerSection]](Nil)((acc, taxYear) => {
      
      val taxYearDates = taxYearRange.taxYearDates(taxYear)
      
      val answerRows: Seq[AnswerRow] = Seq(
        bound.yesNoQuestion(
          pageForTaxYear(taxYear),
          s"${taxYear.messagePrefix}.liability",
          changeUrlForTaxYear(taxYear),
          taxYearDates
        ),
        bound.yesNoQuestion(
          DeclaredTaxToHMRCYesNoPage(taxYear),
          "declaredToHMRC",
          controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(taxYear).url,
          taxYearRange.taxYearDates(taxYear)
        )
      ).flatten
      
      answerRows match {
        case Nil => acc
        case _ => acc :+ AnswerSection(messages("checkYourAnswersSection.heading", taxYearDates: _*), answerRows)
      }
    })
  }

  private def changeUrlForTaxYear(taxYear: TaxYear): String = taxYear match {
    case CYMinus4TaxYear => controllers.routes.CYMinusFourYesNoController.onPageLoad().url
    case CYMinus3TaxYear => controllers.routes.CYMinusThreeYesNoController.onPageLoad().url
    case CYMinus2TaxYear => controllers.routes.CYMinusTwoYesNoController.onPageLoad().url
    case CYMinus1TaxYear => controllers.routes.CYMinusOneYesNoController.onPageLoad().url
  }

  private def pageForTaxYear(taxYear: TaxYear): QuestionPage[Boolean] = taxYear match {
    case CYMinus4TaxYear => CYMinusFourYesNoPage
    case CYMinus3TaxYear => CYMinusThreeYesNoPage
    case CYMinus2TaxYear => CYMinusTwoYesNoPage
    case CYMinus1TaxYear => CYMinusOneYesNoPage
  }

}
