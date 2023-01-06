/*
 * Copyright 2023 HM Revenue & Customs
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

package views

import play.api.mvc.Call
import play.api.test.Helpers.POST
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.EarlierYearsToPayThanAskedYesNoView

class EarlierYearsToPayThanAskedYesNoViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "earlierYearsLiability"
  val year: String = "2017"
  val continueUrl: Call = Call(POST, "#")

  "EarlierYearsToPayThanAskedYesNoView" must {

    val view = viewFor[EarlierYearsToPayThanAskedYesNoView](Some(emptyUserAnswers))

    def applyView: HtmlFormat.Appendable =
      view.apply(year, continueUrl)(fakeRequest, messages)

    behave like normalPage(
      view = applyView,
      messageKeyPrefix = messageKeyPrefix,
      messageKeyParams = year :: Nil,
      expectedGuidanceKeys = "p1"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithSubmitButton(applyView)
  }
}
