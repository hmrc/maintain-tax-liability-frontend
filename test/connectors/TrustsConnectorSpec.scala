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

import base.SpecBase
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.{TrustDetails, YearReturn, YearsReturns}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside}
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class TrustsConnectorSpec extends SpecBase with ScalaFutures
  with Inside with BeforeAndAfterAll with BeforeAndAfterEach with IntegrationPatience {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  override val identifier = "1000000008"

  "trust connector" must {

    "getTrustDetails" in {

      val json = Json.parse(
        """
          |{
          | "startDate": "1920-03-28",
          | "lawCountry": "AD",
          | "administrationCountry": "GB",
          | "residentialStatus": {
          |   "uk": {
          |     "scottishLaw": false,
          |     "preOffShore": "AD"
          |   }
          | },
          | "typeOfTrust": "Will Trust or Intestacy Trust",
          | "deedOfVariation": "Previously there was only an absolute interest under the will",
          | "interVivos": false
          |}
          |""".stripMargin)

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        get(urlEqualTo(s"/trusts/trust-details/$identifier/untransformed"))
          .willReturn(okJson(json.toString))
      )

      val processed = connector.getTrustDetails(identifier)

      whenReady(processed) {
        r =>
          r mustBe TrustDetails(
            startDate = LocalDate.parse("1920-03-28")
          )
      }
    }

    "setYearsReturns" in {

      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsConnector]

      server.stubFor(
        put(urlEqualTo(s"/trusts/tax-liability/$identifier/years-returns"))
          .willReturn(ok)
      )

      val result = connector.setYearsReturns(
        identifier,
        YearsReturns(List(YearReturn("20", taxConsequence = true)))
      )

      result.futureValue.status mustBe OK

      application.stop()
    }

  }
}
