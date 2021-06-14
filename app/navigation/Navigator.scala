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

package navigation

import controllers.routes._
import models._
import pages._
import play.api.mvc.Call

class Navigator {

  def nextPage(page: Page, userAnswers: UserAnswers): Call = routes(page)(userAnswers)

  private def routes: PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation

  private def simpleNavigation: PartialFunction[Page, Call] = {
    case CYMinusFourEarlierYearsPage => CYMinusFourYesNoController.onPageLoad()
    case CYMinusThreeEarlierYearsPage => CYMinusThreeYesNoController.onPageLoad()
    case DeclaredTaxToHMRCYesNoPage(CYMinus4TaxYears) => CYMinusThreeYesNoController.onPageLoad()
    case DeclaredTaxToHMRCYesNoPage(CYMinus3TaxYears) => CYMinusTwoYesNoController.onPageLoad()
    case DeclaredTaxToHMRCYesNoPage(CYMinus2TaxYears) => CYMinusOneYesNoController.onPageLoad()
    case DeclaredTaxToHMRCYesNoPage(CYMinus1TaxYear) => CheckYourAnswersController.onPageLoad()
  }

  private def conditionalNavigation: PartialFunction[Page, UserAnswers => Call] = {
    case CYMinusFourYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CYMinusFourYesNoPage,
      yesCall = DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus4TaxYears),
      noCall = CYMinusThreeYesNoController.onPageLoad()
    )
    case CYMinusThreeYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CYMinusThreeYesNoPage,
      yesCall = DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus3TaxYears),
      noCall = CYMinusTwoYesNoController.onPageLoad()
    )
    case CYMinusTwoYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CYMinusTwoYesNoPage,
      yesCall = DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus2TaxYears),
      noCall = CYMinusOneYesNoController.onPageLoad()
    )
    case CYMinusOneYesNoPage => ua => yesNoNav(
      ua = ua,
      fromPage = CYMinusOneYesNoPage,
      yesCall = DeclaredTaxToHMRCYesNoController.onPageLoad(CYMinus1TaxYear),
      noCall = CheckYourAnswersController.onPageLoad()
    )
  }

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

}
