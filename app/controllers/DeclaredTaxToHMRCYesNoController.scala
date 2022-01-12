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

package controllers

import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import models.{CYMinusNTaxYears, TaxYearRange}
import navigation.Navigator
import pages.DeclaredTaxToHMRCYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeclaredTaxToHMRCYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclaredTaxToHMRCYesNoController @Inject()(
                                                  val controllerComponents: MessagesControllerComponents,
                                                  navigator: Navigator,
                                                  actions: StandardActionSets,
                                                  formProvider: YesNoFormProvider,
                                                  repository: PlaybackRepository,
                                                  view: DeclaredTaxToHMRCYesNoView,
                                                  taxYearRange: TaxYearRange
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def taxYearDates(taxYear: CYMinusNTaxYears)(implicit messages: Messages): Seq[String] = taxYearRange.taxYearDates(taxYear)

  private def form(taxYearDates: Seq[String]): Form[Boolean] = formProvider.withPrefix("declaredToHMRC", taxYearDates)

  def onPageLoad(taxYear: CYMinusNTaxYears): Action[AnyContent] = actions.authorisedWithRequiredData {
    implicit request =>

      val tyd = taxYearDates(taxYear)

      val preparedForm = request.userAnswers.get(DeclaredTaxToHMRCYesNoPage(taxYear)) match {
        case None => form(tyd)
        case Some(value) => form(tyd).fill(value)
      }

      Ok(view(preparedForm, taxYear, tyd: _*))
  }

  def onSubmit(taxYear: CYMinusNTaxYears): Action[AnyContent] = actions.authorisedWithRequiredData.async {
    implicit request =>

      val tyd = taxYearDates(taxYear)

      form(tyd).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(view(formWithErrors, taxYear, tyd: _*)))
        },
        value => {
          val page = DeclaredTaxToHMRCYesNoPage(taxYear)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(page, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(page, updatedAnswers))
        }
      )
  }
}
