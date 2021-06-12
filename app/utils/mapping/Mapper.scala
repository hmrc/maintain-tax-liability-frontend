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

package utils.mapping

import models._
import pages.DeclaredTaxToHMRCYesNoPage
import services.TaxYearService
import utils.Constants.{OCTOBER_5TH_DAY, OCTOBER_5TH_MONTH}

import java.time.LocalDate
import javax.inject.Inject

class Mapper @Inject()(taxYearService: TaxYearService) {

  def apply(userAnswers: UserAnswers): YearsReturns = {

    val october5th = LocalDate.of(
      taxYearService.currentTaxYear.starts.getYear,
      OCTOBER_5TH_MONTH,
      OCTOBER_5TH_DAY
    )

    YearsReturns {
      TaxYear.taxYears.foldLeft[List[YearReturn]](Nil)((acc, taxYear) => {
        if (userAnswers.get(DeclaredTaxToHMRCYesNoPage(taxYear)).contains(false)) {
          taxYear match {
            case CYMinus1TaxYear =>
              acc :+ YearReturn(
                taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.year),
                taxConsequence = taxYearService.currentDate.isAfter(october5th)
              )
            case _ =>
              acc :+ YearReturn(
                taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.year),
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
