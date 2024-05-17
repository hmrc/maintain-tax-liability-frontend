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

package navigation

import base.SpecBase
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator: Navigator = injector.instanceOf[Navigator]

  "Navigator" when {

    "CYMinusFourEarlierYearsPage" must {
      "-> CYMinusFourYesNoController" in {
        navigator.nextPage(CYMinusFourEarlierYearsPage, emptyUserAnswers)
          .mustBe(controllers.routes.CYMinusFourYesNoController.onPageLoad())
      }
    }

    "CYMinusThreeEarlierYearsPage" must {
      "-> CYMinusThreeYesNoController" in {
        navigator.nextPage(CYMinusThreeEarlierYearsPage, emptyUserAnswers)
          .mustBe(controllers.routes.CYMinusThreeYesNoController.onPageLoad())
      }
    }

    "CYMinusFourYesNoPage" when {
      val page = CYMinusFourYesNoPage

      "-> Yes -> DeclaredTaxToHMRCYesNoController" in {
        val answers = emptyUserAnswers.set(page, true).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus4TaxYears))
      }

      "-> No -> CYMinusThreeYesNoController" in {
        val answers = emptyUserAnswers.set(page, false).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.CYMinusThreeYesNoController.onPageLoad())
      }
    }

    "CYMinusThreeYesNoPage" when {
      val page = CYMinusThreeYesNoPage

      "-> Yes -> DeclaredTaxToHMRCYesNoController" in {
        val answers = emptyUserAnswers.set(page, true).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus3TaxYears))
      }

      "-> No -> CYMinusTwoYesNoController" in {
        val answers = emptyUserAnswers.set(page, false).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.CYMinusTwoYesNoController.onPageLoad())
      }
    }

    "CYMinusTwoYesNoPage" when {
      val page = CYMinusTwoYesNoPage

      "-> Yes -> DeclaredTaxToHMRCYesNoController" in {
        val answers = emptyUserAnswers.set(page, true).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus2TaxYears))
      }

      "-> No -> CYMinusOneYesNoController" in {
        val answers = emptyUserAnswers.set(page, false).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.CYMinusOneYesNoController.onPageLoad())
      }
    }

    "CYMinusOneYesNoPage" when {
      val page = CYMinusOneYesNoPage

      "-> Yes -> DeclaredTaxToHMRCYesNoController" in {
        val answers = emptyUserAnswers.set(page, true).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus1TaxYear))
      }

      "-> No -> CheckYourAnswersController" in {
        val answers = emptyUserAnswers.set(page, false).success.value

        navigator.nextPage(page, answers)
          .mustBe(controllers.routes.CheckYourAnswersController.onPageLoad())
      }
    }

    "DeclaredTaxToHMRCYesNoPage" when {

      val page = DeclaredTaxToHMRCYesNoPage

      "CYMinus4TaxYear" must {
        "-> CYMinusThreeYesNoController" in {
          navigator.nextPage(page(CYMinus4TaxYears), emptyUserAnswers)
            .mustBe(controllers.routes.CYMinusThreeYesNoController.onPageLoad())
        }
      }

      "CYMinus3TaxYear" must {
        "-> CYMinusTwoYesNoController" in {
          navigator.nextPage(page(CYMinus3TaxYears), emptyUserAnswers)
            .mustBe(controllers.routes.CYMinusTwoYesNoController.onPageLoad())
        }
      }

      "CYMinus2TaxYear" must {
        "-> CYMinusOneYesNoController" in {
          navigator.nextPage(page(CYMinus2TaxYears), emptyUserAnswers)
            .mustBe(controllers.routes.CYMinusOneYesNoController.onPageLoad())
        }
      }

      "CYMinus1TaxYear" must {
        "-> CheckYourAnswersController" in {
          navigator.nextPage(page(CYMinus1TaxYear), emptyUserAnswers)
            .mustBe(controllers.routes.CheckYourAnswersController.onPageLoad())
        }
      }
    }

  }
}
