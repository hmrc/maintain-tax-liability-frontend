/*
 * Copyright 2025 HM Revenue & Customs
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

import config.ErrorHandler
import connectors.{TrustsConnector, TrustsStoreConnector}
import controllers.actions.StandardActionSets
import controllers.routes._
import models.TaskStatus.{Completed, InProgress, TaskStatus}
import models.UserAnswers
import play.api.mvc._
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.{Session, SessionLogging}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 mcc: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 repository: PlaybackRepository,
                                 trustsConnector: TrustsConnector,
                                 errorHandler: ErrorHandler,
                                 trustsStoreConnector: TrustsStoreConnector
                               )(implicit ec: ExecutionContext) extends FrontendController(mcc) with SessionLogging {

  def onPageLoad(identifier: String): Action[AnyContent] = actions.authorisedWithSavedSession(identifier).async {
    implicit request =>

      def redirect(taskStatus: TaskStatus): Future[Result] = {

        def futureRedirect(call: Call): Future[Result] = Future.successful(Redirect(call))

        request.userAnswers match {
          case Some(_) if taskStatus == Completed =>
            Future.successful(Redirect(routes.CheckYourAnswersController.onPageLoad()))
          case ua =>
            for {
              _ <- repository.set(ua.getOrElse(UserAnswers(request.user.internalId, identifier, Session.id(hc), newId = s"${request.user.internalId}-$identifier-${Session.id(hc)}")))
              firstTaxYearAvailable <- trustsConnector.getFirstTaxYearToAskFor(identifier)
              result <- firstTaxYearAvailable.yearsAgo match {
                case 4 if firstTaxYearAvailable.earlierYearsToDeclare =>
                  futureRedirect(CYMinusFourEarlierYearsController.onPageLoad())
                case 4 =>
                  futureRedirect(CYMinusFourYesNoController.onPageLoad())
                case 3 if firstTaxYearAvailable.earlierYearsToDeclare =>
                  futureRedirect(CYMinusThreeEarlierYearsController.onPageLoad())
                case 3 =>
                  futureRedirect(CYMinusThreeYesNoController.onPageLoad())
                case 2 =>
                  futureRedirect(CYMinusTwoYesNoController.onPageLoad())
                case 1 =>
                  futureRedirect(CYMinusOneYesNoController.onPageLoad())
                case x =>
                  errorLog(s"Unexpected result for number of years ago of first tax year available: $x", Some(identifier))
                  errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html))
              }
            } yield {
              result
            }
        }
      }

      for {
        taskStatus <- trustsStoreConnector.getTaskStatus(identifier)
        _ <- trustsStoreConnector.updateTaskStatus(identifier, InProgress)
        result <- redirect(taskStatus)
      } yield result
  }

}
