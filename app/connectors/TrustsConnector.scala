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

import config.AppConfig
import models.{TrustDetails, YearsReturns}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsConnector @Inject()(http: HttpClient, config: AppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val baseUrl: String = s"$trustsUrl/tax-liability"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetails] = {
    val url = s"$trustsUrl/trust-details/$identifier/untransformed"
    http.GET[TrustDetails](url)
  }

  def setYearsReturns(identifier: String, value: YearsReturns)
                     (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val url = s"$baseUrl/$identifier/years-returns"
    http.PUT[YearsReturns, HttpResponse](url, value)
  }

}
