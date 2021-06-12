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

package controllers

import config.AppConfig
import connectors.{TrustsConnector, TrustsStoreConnector}
import controllers.actions.StandardActionSets
import pages.TaskCompleted
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mapping.Mapper
import utils.print.PrintHelper
import views.html.CheckYourAnswersView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject()(
                                            val controllerComponents: MessagesControllerComponents,
                                            actions: StandardActionSets,
                                            printHelper: PrintHelper,
                                            mapper: Mapper,
                                            repository: PlaybackRepository,
                                            appConfig: AppConfig,
                                            trustsConnector: TrustsConnector,
                                            trustsStoreConnector: TrustsStoreConnector,
                                            view: CheckYourAnswersView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authorisedWithRequiredData {
    implicit request =>
      Ok(view(printHelper(request.userAnswers)))
  }

  def onSubmit(): Action[AnyContent] = actions.authorisedWithRequiredData.async {
    implicit request =>

      val userAnswers = request.userAnswers
      val identifier = userAnswers.identifier

      for {
        _ <- trustsConnector.setYearsReturns(identifier, mapper(userAnswers))
        _ <- trustsStoreConnector.setTaskComplete(identifier)
        updatedAnswers <- Future.fromTry(userAnswers.set(TaskCompleted, true))
        _ <- repository.set(updatedAnswers)
      } yield Redirect(appConfig.maintainATrustOverviewUrl)
  }

}