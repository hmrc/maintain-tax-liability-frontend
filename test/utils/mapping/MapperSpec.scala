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

package utils.mapping

import java.time.LocalDate
import base.SpecBase
import generators.DateGenerators
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.DeclaredTaxToHMRCYesNoPage
import services.TaxYearService

class MapperSpec extends SpecBase with ScalaCheckPropertyChecks with DateGenerators with BeforeAndAfterEach {

  val mockTaxYearService: TaxYearService = Mockito.mock(classOf[TaxYearService])
  val mapper                             = new Mapper(mockTaxYearService)

  override def beforeEach(): Unit = {
    reset(mockTaxYearService)
    when(mockTaxYearService.currentTaxYear).thenCallRealMethod()
    when(mockTaxYearService.nTaxYearsAgoFinishYear(any())).thenCallRealMethod()
  }

  "Mapper" must {

    "map user answers to YearsReturns" when {

      "need to pay tax for CY-2, CY-3 and CY-4" must {
        "create returns with tax consequences for these years" in
          forAll(arbitrary[LocalDate](arbitraryDateInTaxYear)) { date =>
            when(mockTaxYearService.currentDate).thenReturn(date)

            val userAnswers = emptyUserAnswers
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYears), false)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYears), false)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYears), false)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear), true)
              .success
              .value

            val result = mapper(userAnswers)

            result mustEqual YearsReturns(
              List(
                YearReturn(taxReturnYear = "17", taxConsequence = true),
                YearReturn(taxReturnYear = "18", taxConsequence = true),
                YearReturn(taxReturnYear = "19", taxConsequence = true)
              )
            )
          }
      }

      "need to pay tax for CY-1 (on or before October 5th)" must {
        "create return without tax consequence for that year" in
          forAll(arbitrary[LocalDate](arbitraryDateInTaxYearOnOrBeforeOctober5th)) { date =>
            when(mockTaxYearService.currentDate).thenReturn(date)

            val userAnswers = emptyUserAnswers
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear), false)
              .success
              .value

            val result = mapper(userAnswers)

            result mustEqual YearsReturns(
              List(
                YearReturn(taxReturnYear = "20", taxConsequence = false)
              )
            )
          }
      }

      "need to pay tax for CY-1 (after October 5th)" must {
        "create return with tax consequence for that year" in
          forAll(arbitrary[LocalDate](arbitraryDateInTaxYearAfterOctober5th)) { date =>
            when(mockTaxYearService.currentDate).thenReturn(date)

            val userAnswers = emptyUserAnswers
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear), false)
              .success
              .value

            val result = mapper(userAnswers)

            result mustEqual YearsReturns(
              List(
                YearReturn(taxReturnYear = "20", taxConsequence = true)
              )
            )
          }
      }

      "all tax paid" must {
        "create no returns" in
          forAll(arbitrary[LocalDate](arbitraryDateInTaxYear)) { date =>
            when(mockTaxYearService.currentDate).thenReturn(date)

            val userAnswers = emptyUserAnswers
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYears), true)
              .success
              .value
              .set(DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear), true)
              .success
              .value

            val result = mapper(userAnswers)

            result mustEqual YearsReturns(Nil)
          }
      }
    }
  }

}
