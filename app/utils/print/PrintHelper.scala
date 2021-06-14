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

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Seq[AnswerSection] = {

    val bound = answerRowConverter.bind(userAnswers)
    
    CYMinusNTaxYears.taxYears.foldLeft[Seq[AnswerSection]](Nil)((acc, taxYear) => {
      
      val taxYearDates = taxYearRange.taxYearDates(taxYear)
      
      val answerRows: Seq[AnswerRow] = Seq(
        bound.yesNoQuestion(
          query = taxYear.page,
          labelKey = s"${taxYear.messagePrefix}.liability",
          changeUrl = taxYear.changeUrl,
          arguments = taxYearDates
        ),
        bound.yesNoQuestion(
          query = DeclaredTaxToHMRCYesNoPage(taxYear),
          labelKey = "declaredToHMRC",
          changeUrl = controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(taxYear).url,
          arguments = taxYearDates
        )
      ).flatten
      
      answerRows match {
        case Nil => acc
        case _ => acc :+ AnswerSection(messages("checkYourAnswersSection.heading", taxYearDates: _*), answerRows)
      }
    })
  }

}
