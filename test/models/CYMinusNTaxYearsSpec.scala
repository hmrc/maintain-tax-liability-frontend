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
import play.api.mvc.PathBindable

class CYMinusNTaxYearsSpec extends SpecBase {

  val pathBindable: PathBindable[CYMinusNTaxYears] = implicitly[PathBindable[CYMinusNTaxYears]]

  "CYMinusNTaxYears pathBindable" when {

    "bind" must {

      "return CYMinus4TaxYears for 4" in {
        pathBindable.bind("taxYear", "4") mustBe Right(CYMinus4TaxYears)
      }

      "return CYMinus3TaxYears for 3" in {
        pathBindable.bind("taxYear", "3") mustBe Right(CYMinus3TaxYears)
      }

      "return CYMinus2TaxYears for 2" in {
        pathBindable.bind("taxYear", "2") mustBe Right(CYMinus2TaxYears)
      }

      "return CYMinus1TaxYear for 1" in {
        pathBindable.bind("taxYear", "1") mustBe Right(CYMinus1TaxYear)
      }

      "return Left for an invalid value" in {
        pathBindable.bind("taxYear", "5") mustBe Left("Not a valid tax year")
      }

      "return Left for a non-integer" in {
        pathBindable.bind("taxYear", "abc").isLeft mustBe true
      }
    }

    "unbind" must {
      "return the string value" in {
        pathBindable.unbind("taxYear", CYMinus1TaxYear) mustBe "1"
      }
    }
  }

}