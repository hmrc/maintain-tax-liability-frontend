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

package pages

import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear}
import pages.behaviours.PageBehaviours

class DidDeclareTaxToHMRCYesNoPageSpec extends PageBehaviours {

  private val taxYears = Seq(
    CYMinus4TaxYear,
    CYMinus3TaxYear,
    CYMinus2TaxYear,
    CYMinus1TaxYear
  )

  taxYears.foreach { taxYear =>
    s"DidDeclareTaxToHMRCYesNoPage for ${taxYear.messagePrefix}" must {

      beRetrievable[Boolean](DidDeclareTaxToHMRCYesNoPage(taxYear))

      beSettable[Boolean](DidDeclareTaxToHMRCYesNoPage(taxYear))

      beRemovable[Boolean](DidDeclareTaxToHMRCYesNoPage(taxYear))
    }
  }
}
