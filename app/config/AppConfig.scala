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

package config

import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(val config: Configuration, servicesConfig: ServicesConfig, contactFrontendConfig: ContactFrontendConfig) {

  val en: String = "en"
  val cy: String = "cy"

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(en),
    "cymraeg" -> Lang(cy)
  )

  val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  val appName: String = config.get[String]("appName")

  lazy val loginUrl: String = config.get[String]("urls.login")
  lazy val loginContinueUrl: String = config.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = config.get[String]("urls.logout")
  lazy val maintainATrustOverviewUrl: String = config.get[String]("urls.maintainATrustOverview")

  lazy val trustsUrl: String = servicesConfig.baseUrl("trusts")
  lazy val trustsAuthUrl: String = servicesConfig.baseUrl("trusts-auth")
  lazy val trustsStoreUrl: String = servicesConfig.baseUrl("trusts-store")

  lazy val logoutAudit: Boolean = config.get[Boolean]("features.auditing.logout")

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val countdownLength: Int = config.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = config.get[Int]("timeout.length")

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case `cy` => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }
    config.get[String](path)
  }

  val cachettlSessionInSeconds: Long = config.get[Long]("mongodb.session.ttlSeconds")

  val cachettlplaybackInSeconds: Long = config.get[Long]("mongodb.playback.ttlSeconds")

  val dropIndexes: Boolean = config.getOptional[Boolean]("microservice.services.features.mongo.dropIndexes").getOrElse(false)

}
