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

import controllers.actions.StandardActionSets
import models.UserAnswers
import pages.TaskCompleted
import play.api.mvc._
import repositories.PlaybackRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.SessionLogging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndexController @Inject()(
                                 mcc: MessagesControllerComponents,
                                 actions: StandardActionSets,
                                 repository: PlaybackRepository
                               )(implicit ec: ExecutionContext) extends FrontendController(mcc) with SessionLogging {

  def onPageLoad(identifier: String): Action[AnyContent] = actions.authorisedWithSavedSession(identifier).async {
    implicit request =>

      request.userAnswers match {
        case Some(ua) if ua.get(TaskCompleted).contains(true) =>
          Future.successful(Redirect(routes.CheckYourAnswersController.onPageLoad()))
        case _ =>
          repository.set(UserAnswers(request.user.internalId, identifier)) map { _ =>
            Redirect(routes.FeatureNotAvailableController.onPageLoad())
          }
      }
  }

}
