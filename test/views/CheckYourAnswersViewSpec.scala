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

package views

import play.twirl.api.HtmlFormat
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.SummaryListViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends SummaryListViewBehaviours {

  val messageKeyPrefix = "checkYourAnswers"

  val answerSections: Seq[AnswerSection] = (1 to 4).foldLeft[Seq[AnswerSection]](Nil)((acc, i) => {
    acc :+ AnswerSection(
      heading = s"Heading $i",
      rows = Seq(
        AnswerRow(
          label = s"Label $i",
          answer = HtmlFormat.escape(s"Answer $i"),
          changeUrl = s"change-url-$i"
        )
      )
    )
  })

  "CheckAnswers view" must {

    val view = viewFor[CheckYourAnswersView](Some(emptyUserAnswers))

    val applyView = view.apply(answerSections)(fakeRequest, messages)

    behave like normalPage(applyView, messageKeyPrefix, Nil)

    behave like summaryListPage(applyView, answerSections)

    behave like pageWithBackLink(applyView)

    behave like pageWithSubmitButton(applyView)
  }
}
