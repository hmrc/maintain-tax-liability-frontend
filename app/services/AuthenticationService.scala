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

package services

import com.google.inject.Inject
import config.ErrorHandler
import connectors.TrustsAuthConnector
import models.requests.DataRequest
import models.responses.{TrustsAuthAgentAllowed, TrustsAuthAllowed, TrustsAuthDenied}
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import utils.SessionLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationServiceImpl @Inject()(
                                           trustAuthConnector: TrustsAuthConnector,
                                           errorHandler: ErrorHandler
                                         ) extends AuthenticationService with SessionLogging {

  override def authenticateAgent[A]()
                                   (implicit request: Request[A], hc: HeaderCarrier): Future[Either[Result, String]] = {
    trustAuthConnector.agentIsAuthorised().flatMap {
      case TrustsAuthAgentAllowed(arn) => Future.successful(Right(arn))
      case TrustsAuthDenied(redirectUrl) => Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        warnLog("Unable to authenticate agent with trusts-auth")
        Future.successful(Left(InternalServerError(errorHandler.internalServerErrorTemplate)))
    }  }

  override def authenticateForIdentifier[A](identifier: String)
                                           (implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]] = {
    trustAuthConnector.authorisedForIdentifier(identifier).flatMap {
      case _: TrustsAuthAllowed => Future.successful(Right(request))
      case TrustsAuthDenied(redirectUrl) => Future.successful(Left(Redirect(redirectUrl)))
      case _ =>
        warnLog("Unable to authenticate with trusts-auth", Some(identifier))
        Future.successful(Left(InternalServerError(errorHandler.internalServerErrorTemplate)))
    }
  }

}

trait AuthenticationService {
  def authenticateAgent[A]()
                          (implicit request: Request[A], hc: HeaderCarrier): Future[Either[Result, String]]

  def authenticateForIdentifier[A](identifier: String)
                                  (implicit request: DataRequest[A], hc: HeaderCarrier): Future[Either[Result, DataRequest[A]]]
}
