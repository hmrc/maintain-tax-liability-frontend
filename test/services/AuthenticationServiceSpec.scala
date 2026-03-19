/*
 * Copyright 2026 HM Revenue & Customs
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

import base.SpecBase
import config.ErrorHandler
import connectors.TrustsAuthConnector
import models.OrganisationUser
import models.requests.DataRequest
import models.responses.{TrustsAuthAgentAllowed, TrustsAuthAllowed, TrustsAuthDenied, TrustsAuthInternalServerError}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class AuthenticationServiceSpec extends SpecBase {

  val mockTrustsAuthConnector: TrustsAuthConnector = Mockito.mock(classOf[TrustsAuthConnector])
  val mockErrorHandler: ErrorHandler               = Mockito.mock(classOf[ErrorHandler])

  val service = new AuthenticationServiceImpl(mockTrustsAuthConnector, mockErrorHandler)

  implicit val hc: HeaderCarrier                            = HeaderCarrier()
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  def dataRequest: DataRequest[AnyContentAsEmpty.type] =
    DataRequest(FakeRequest("GET", "/"), emptyUserAnswers, OrganisationUser(internalId, Enrolments(Set())))

  "AuthenticationService" when {

    "authenticateAgent" when {

      "trusts-auth returns TrustsAuthAgentAllowed" must {
        "return Right with the ARN" in {
          when(mockTrustsAuthConnector.agentIsAuthorised()(any(), any()))
            .thenReturn(Future.successful(TrustsAuthAgentAllowed("SomeARN")))

          val result = service.authenticateAgent()

          whenReady(result) { res =>
            res mustBe Right("SomeARN")
          }
        }
      }

      "trusts-auth returns TrustsAuthDenied" must {
        "return Left with a redirect" in {
          when(mockTrustsAuthConnector.agentIsAuthorised()(any(), any()))
            .thenReturn(Future.successful(TrustsAuthDenied("/redirect-url")))

          val result = service.authenticateAgent()

          whenReady(result) { res =>
            res.isLeft mustBe true
          }
        }
      }

      "trusts-auth returns TrustsAuthInternalServerError" must {
        "return Left with an internal server error" in {
          when(mockTrustsAuthConnector.agentIsAuthorised()(any(), any()))
            .thenReturn(Future.successful(TrustsAuthInternalServerError))

          when(mockErrorHandler.internalServerErrorTemplate(any()))
            .thenReturn(Future.successful(Html("error")))

          val result = service.authenticateAgent()

          whenReady(result) { res =>
            res.isLeft mustBe true
          }
        }
      }
    }

    "authenticateForIdentifier" when {

      "trusts-auth returns TrustsAuthAllowed" must {
        "return Right with the request" in {
          implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest

          when(mockTrustsAuthConnector.authorisedForIdentifier(any())(any(), any()))
            .thenReturn(Future.successful(TrustsAuthAllowed()))

          val result = service.authenticateForIdentifier(identifier)

          whenReady(result) { res =>
            res.isRight mustBe true
          }
        }
      }

      "trusts-auth returns TrustsAuthDenied" must {
        "return Left with a redirect" in {
          implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest

          when(mockTrustsAuthConnector.authorisedForIdentifier(any())(any(), any()))
            .thenReturn(Future.successful(TrustsAuthDenied("/redirect-url")))

          val result = service.authenticateForIdentifier(identifier)

          whenReady(result) { res =>
            res.isLeft mustBe true
          }
        }
      }

      "trusts-auth returns TrustsAuthInternalServerError" must {
        "return Left with an internal server error" in {
          implicit val dr: DataRequest[AnyContentAsEmpty.type] = dataRequest

          when(mockTrustsAuthConnector.authorisedForIdentifier(any())(any(), any()))
            .thenReturn(Future.successful(TrustsAuthInternalServerError))

          when(mockErrorHandler.internalServerErrorTemplate(any()))
            .thenReturn(Future.successful(Html("error")))

          val result = service.authenticateForIdentifier(identifier)

          whenReady(result) { res =>
            res.isLeft mustBe true
          }
        }
      }
    }
  }

}
