/*
 * Copyright 2024 HM Revenue & Customs
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

package utils.mapping

import models._
import pages.DeclaredTaxToHMRCYesNoPage
import services.TaxYearService

import javax.inject.Inject

class Mapper @Inject()(taxYearService: TaxYearService) {

  def apply(userAnswers: UserAnswers): YearsReturns = {

    val halfwayThroughTaxYear = taxYearService.currentTaxYear.finishes.minusMonths(6) // October 5th

    YearsReturns {
      CYMinusNTaxYears.taxYears.foldLeft[List[YearReturn]](Nil)((acc, taxYear) => {
        if (userAnswers.get(DeclaredTaxToHMRCYesNoPage(taxYear)).contains(false)) {
          taxYear match {
            case CYMinus1TaxYear =>
              acc :+ YearReturn(
                taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.n),
                taxConsequence = taxYearService.currentDate.isAfter(halfwayThroughTaxYear)
              )
            case _ =>
              acc :+ YearReturn(
                taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.n),
                taxConsequence = true
              )
          }
        } else {
          acc
        }
      })
    }
  }

}
