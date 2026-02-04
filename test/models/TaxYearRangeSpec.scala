/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import org.mockito.Mockito
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.i18n.{Lang, MessagesImpl}
import services.TaxYearService
import uk.gov.hmrc.play.language.LanguageUtils

class TaxYearRangeSpec extends SpecBase with BeforeAndAfterEach {

  val languageUtils: LanguageUtils       = injector.instanceOf[LanguageUtils]
  val mockTaxYearService: TaxYearService = Mockito.mock(classOf[TaxYearService])

  override def beforeEach(): Unit = {
    reset(mockTaxYearService)
    when(mockTaxYearService.currentTaxYear).thenReturn(uk.gov.hmrc.time.TaxYear(2020))
  }

  val taxYearRange = new TaxYearRange(languageUtils, mockTaxYearService)

  "TaxYearRange" when {

    ".taxYearDates" must {
      "return tax year start date and end date as list of strings" when {

        def messages(langCode: String): MessagesImpl = {
          val lang: Lang = Lang(langCode)
          MessagesImpl(lang, messagesApi)
        }

        "CYMinus1TaxYear" when {

          val taxYear = CYMinus1TaxYear

          "English" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2019", "5 April 2020")
          }

          "Welsh" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2019", "5 Ebrill 2020")
          }
        }

        "CYMinus2TaxYear" when {

          val taxYear = CYMinus2TaxYears

          "English" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2018", "5 April 2019")
          }

          "Welsh" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2018", "5 Ebrill 2019")
          }
        }

        "CYMinus3TaxYear" when {

          val taxYear = CYMinus3TaxYears

          "English" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2017", "5 April 2018")
          }

          "Welsh" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2017", "5 Ebrill 2018")
          }
        }

        "CYMinus4TaxYear" when {

          val taxYear = CYMinus4TaxYears

          "English" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("en"))
            result mustEqual Seq("6 April 2016", "5 April 2017")
          }

          "Welsh" in {
            val result = taxYearRange.taxYearDates(taxYear)(messages("cy"))
            result mustEqual Seq("6 Ebrill 2016", "5 Ebrill 2017")
          }
        }
      }
    }

    ".yearAtStart" must {
      "return the year at the start of the tax year" when {

        "CYMinus1TaxYear" in {

          val result = taxYearRange.yearAtStart(CYMinus1TaxYear)
          result mustEqual "2019"
        }

        "CYMinus2TaxYear" in {

          val result = taxYearRange.yearAtStart(CYMinus2TaxYears)
          result mustEqual "2018"
        }

        "CYMinus3TaxYear" in {

          val result = taxYearRange.yearAtStart(CYMinus3TaxYears)
          result mustEqual "2017"
        }

        "CYMinus4TaxYear" in {

          val result = taxYearRange.yearAtStart(CYMinus4TaxYears)
          result mustEqual "2016"
        }
      }
    }
  }

}
