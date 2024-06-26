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

package connectors

import base.{SpecBase, WireMockHelper}
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.responses._
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

class TrustsAuthConnectorSpec extends SpecBase with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val authorisedUrl: String = s"/trusts-auth/agent-authorised"

  private def authorisedUrlFor(identifier: String): String = s"/trusts-auth/authorised/$identifier"

  private def responseFromJson(json: JsValue): ResponseDefinitionBuilder = {
    aResponse().withStatus(Status.OK).withBody(json.toString())
  }

  private def allowedResponse: ResponseDefinitionBuilder = responseFromJson(Json.obj("authorised" -> true))

  private def allowedAgentResponse: ResponseDefinitionBuilder = responseFromJson(Json.obj("arn" -> "SomeArn"))

  private def redirectResponse(): ResponseDefinitionBuilder = responseFromJson(Json.obj("redirectUrl" -> "redirect-url"))

  private def wiremock(url: String, response: ResponseDefinitionBuilder): StubMapping = {
    server.stubFor(get(urlEqualTo(url)).willReturn(response))
  }

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(defaultAppConfigurations ++ Map("microservice.services.trusts-auth.port" -> server.port()))
    .build()

  private lazy val connector = app.injector.instanceOf[TrustsAuthConnector]

  override val identifier = "0123456789"

  "TrustsAuthConnector" should {
    "authorisedForIdentifier" when {

      "returns 'Allowed' when" when {
        "service returns with no redirect url" in {

          wiremock(authorisedUrlFor(identifier), allowedResponse)

          connector.authorisedForIdentifier(identifier) map { result =>
            result mustEqual TrustsAuthAllowed()
          }
        }
      }
      "returns 'Denied' when" when {
        "service returns a redirect url" in {

          wiremock(authorisedUrlFor(identifier), redirectResponse())

          connector.authorisedForIdentifier(identifier) map { result =>
            result mustEqual TrustsAuthDenied("redirect-url")
          }
        }
      }
      "returns 'Internal server error' when" when {
        "service returns something not OK" in {

          wiremock(authorisedUrlFor(identifier), aResponse().withStatus(Status.INTERNAL_SERVER_ERROR))

          connector.authorisedForIdentifier(identifier) map { result =>
            result mustEqual TrustsAuthInternalServerError
          }
        }
      }
    }
    "authorised" when {

      "returns 'Agent Allowed' when" when {
        "service returns with agent authorised response" in {

          wiremock(authorisedUrl, allowedAgentResponse)

          connector.agentIsAuthorised() map { result =>
            result mustEqual TrustsAuthAgentAllowed("SomeArn")
          }
        }
      }

      "returns 'Denied' when" when {
        "service returns a redirect url" in {

          wiremock(authorisedUrl, redirectResponse())

          connector.agentIsAuthorised() map { result =>
            result mustEqual TrustsAuthDenied("redirect-url")
          }
        }
      }
      "returns 'Internal server error' when" when {
        "service returns something not OK" in {

          wiremock(authorisedUrl, aResponse().withStatus(Status.INTERNAL_SERVER_ERROR))

          connector.agentIsAuthorised() map { result =>
            result mustEqual TrustsAuthInternalServerError
          }
        }
      }
    }
  }
}
