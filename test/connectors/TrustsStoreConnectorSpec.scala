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

package connectors

import base.{SpecBase, WireMockHelper}
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, urlEqualTo, _}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

class TrustsStoreConnectorSpec extends SpecBase
  with ScalaFutures
  with IntegrationPatience
  with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "trusts store connector" when {

    ".setTasksComplete" must {

      val url = s"/trusts-store/maintain/tasks/tax-liability/$identifier"

      "return OK with the current task status" in {
        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts-store.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustsStoreConnector]

        val json = Json.parse(
          """
            |{
            |  "trustDetails": false,
            |  "trustees": false,
            |  "beneficiaries": false,
            |  "settlors": false,
            |  "protectors": false,
            |  "other": false,
            |  "nonEeaCompany": false,
            |  "taxLiability": true
            |}
            |""".stripMargin)

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(okJson(json.toString))
        )

        val futureResult = connector.setTaskComplete(identifier)

        whenReady(futureResult) {
          r =>
            r.status mustBe 200
        }

        application.stop()
      }

      "return default tasks when a failure occurs" in {
        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts-store.port" -> server.port(),
              "auditing.enabled" -> false
            ): _*
          ).build()

        val connector = application.injector.instanceOf[TrustsStoreConnector]

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(serverError())
        )

        connector.setTaskComplete(identifier) map { response =>
          response.status mustBe 500
        }

        application.stop()
      }
    }
  }
}