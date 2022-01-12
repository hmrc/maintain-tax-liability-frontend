/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import play.api.i18n.Messages
import services.TaxYearService
import uk.gov.hmrc.play.language.LanguageUtils

import javax.inject.Inject

class TaxYearRange @Inject()(languageUtils: LanguageUtils, taxYearService: TaxYearService) {

  def taxYearDates(cYMinusNTaxYears: CYMinusNTaxYears)(implicit messages: Messages): Seq[String] = {

    val taxYear = taxYearService.currentTaxYear.back(cYMinusNTaxYears.n)

    lazy val startDate: String = languageUtils.Dates.formatDate(taxYear.starts)
    lazy val endDate: String = languageUtils.Dates.formatDate(taxYear.finishes)

    startDate :: endDate :: Nil
  }

  def yearAtStart(cYMinusNTaxYears: CYMinusNTaxYears): String = taxYearService.currentTaxYear.back(cYMinusNTaxYears.n).startYear.toString

}
