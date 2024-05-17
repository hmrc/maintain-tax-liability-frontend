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

package controllers

import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import models.{CYMinus2TaxYears, TaxYearRange}
import navigation.Navigator
import pages.CYMinusTwoYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CYMinusTwoYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CYMinusTwoYesNoController @Inject()(
                                           val controllerComponents: MessagesControllerComponents,
                                           navigator: Navigator,
                                           actions: StandardActionSets,
                                           formProvider: YesNoFormProvider,
                                           repository: PlaybackRepository,
                                           view: CYMinusTwoYesNoView,
                                           taxYearRange: TaxYearRange
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def taxYearDates(implicit messages: Messages): Seq[String] = taxYearRange.taxYearDates(CYMinus2TaxYears)

  private def form(implicit messages: Messages): Form[Boolean] = formProvider.withPrefix("cyMinusTwo.liability", taxYearDates)

  def onPageLoad(): Action[AnyContent] = actions.authorisedWithRequiredData {
    implicit request =>

      val preparedForm = request.userAnswers.get(CYMinusTwoYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, taxYearDates: _*))
  }

  def onSubmit(): Action[AnyContent] = actions.authorisedWithRequiredData.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, taxYearDates: _*))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusTwoYesNoPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusTwoYesNoPage, updatedAnswers))
      )
  }
}
