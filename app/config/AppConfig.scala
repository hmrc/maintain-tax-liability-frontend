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

package config

import play.api.Configuration
import play.api.mvc.Request
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.{URI, URLEncoder}
import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  lazy val loginUrl: String = config.get[String]("urls.login")
  lazy val loginContinueUrl: String = config.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = config.get[String]("urls.logout")

  lazy val trustsUrl: String = servicesConfig.baseUrl("trusts")
  lazy val trustsAuthUrl: String = servicesConfig.baseUrl("trusts-auth")

  lazy val logoutAudit: Boolean = config.get[Boolean]("microservice.services.features.auditing.logout")

  private lazy val contactHost: String = config.get[String]("microservice.services.contact-frontend.host")
  private lazy val contactFormServiceIdentifier: String = "trusts"
  lazy val betaFeedbackUrl: String = s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUnauthenticatedUrl: String = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  def accessibilityLinkUrl(implicit request: Request[_]): String = {
    lazy val accessibilityBaseLinkUrl: String = config.get[String]("urls.accessibility")
    val userAction = URLEncoder.encode(new URI(request.uri).getPath, "UTF-8")
    s"$accessibilityBaseLinkUrl?userAction=$userAction"
  }

  lazy val countdownLength: Int = config.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = config.get[Int]("timeout.length")

}