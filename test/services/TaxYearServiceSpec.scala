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

package services

import base.SpecBase
import generators.DateGenerators
import models.FirstTaxYearAvailable
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.time.TaxYear

import java.time.LocalDate

class TaxYearServiceSpec extends SpecBase with ScalaCheckPropertyChecks with DateGenerators with BeforeAndAfterEach {

  val taxYearService: TaxYearService = mock[TaxYearService]

  override def beforeEach(): Unit = {
    reset(taxYearService)
    when(taxYearService.currentTaxYear).thenReturn(TaxYear(arbitraryStartYear))
    when(taxYearService.nTaxYearsAgoFinishYear(any())).thenCallRealMethod()
    when(taxYearService.firstTaxYearAvailable(any())).thenCallRealMethod()
  }

  "TaxYearService" when {

    "nTaxYearsAgoFinishYear" must {
      "return last two digits of tax year finish year" in {
        case class Test(input: Int, expectedOutput: String)

        val tests = Seq(
          Test(1, "20"),
          Test(2, "19"),
          Test(3, "18"),
          Test(4, "17")
        )

        tests.foreach { test =>
          val result = taxYearService.nTaxYearsAgoFinishYear(test.input)
          result mustBe test.expectedOutput
        }
      }
    }

    "firstTaxYearAvailable" must {

      "return first tax year available for start date" when {

        "start year of start date tax year earlier than start year of oldest tax year to show" when {

          "before December deadline" in {

            forAll(
              arbitrary[LocalDate](arbitraryDateInTaxYearOnOrBeforeDecember22nd),
              Gen.choose(5, 100) // any number of years ago over 4 (100 is arbitrary)
            ) {
              (date, yearsAgo) =>
                beforeEach()

                when(taxYearService.currentDate).thenReturn(date)
                val startDate = date.minusYears(yearsAgo)

                val result = taxYearService.firstTaxYearAvailable(startDate)

                result mustBe FirstTaxYearAvailable(yearsAgo = 4, earlierYearsToDeclare = true)
            }
          }

          "after December deadline" in {

            forAll(
              arbitrary[LocalDate](arbitraryDateInTaxYearAfterDecember22nd),
              Gen.choose(5, 100) // any number of years ago over 4 (100 is arbitrary)
            ) {
              (date, yearsAgo) =>
                beforeEach()

                when(taxYearService.currentDate).thenReturn(date)
                val startDate = date.minusYears(yearsAgo)

                val result = taxYearService.firstTaxYearAvailable(startDate)

                result mustBe FirstTaxYearAvailable(yearsAgo = 3, earlierYearsToDeclare = true)
            }
          }
        }

        "start year of start date tax year not earlier than start year of oldest tax year to show" in {

          forAll(
            arbitrary[LocalDate](arbitraryDateInTaxYear),
            Gen.choose(0, 4) // any number of years ago under 4
          ) {
            (date, yearsAgo) =>
              beforeEach()

              when(taxYearService.currentDate).thenReturn(date)
              val startDate = date.minusYears(yearsAgo)

              val result = taxYearService.firstTaxYearAvailable(startDate)

              result mustBe FirstTaxYearAvailable(yearsAgo = yearsAgo, earlierYearsToDeclare = false)
          }
        }
      }
    }
  }

}
