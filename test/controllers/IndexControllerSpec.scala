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

import base.SpecBase
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val indexRoute: String = routes.IndexController.onPageLoad(identifier).url

  override def beforeEach(): Unit = {
    reset(playbackRepository)
    when(playbackRepository.set(any())).thenReturn(Future.successful(true))
  }

  "IndexController" when {

    "there are no previously saved answers" must {
      "set new user answers in repository and redirect to feature unavailable" in {

        val application = applicationBuilder(userAnswers = None)
          .build()

        val request = FakeRequest(GET, indexRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.FeatureNotAvailableController.onPageLoad().url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(playbackRepository).set(uaCaptor.capture)
        uaCaptor.getValue.internalId mustBe internalId
        uaCaptor.getValue.identifier mustBe identifier

        application.stop()
      }
    }

    "there are previously saved answers" must {
      "set existing answers in repository and redirect to feature unavailable" in {

        val internalId = "existingInternalId"
        val identifier = "existingIdentifier"

        val userAnswers = emptyUserAnswers.copy(internalId = internalId, identifier = identifier)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .build()

        val request = FakeRequest(GET, indexRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustBe routes.FeatureNotAvailableController.onPageLoad().url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(playbackRepository).set(uaCaptor.capture)
        uaCaptor.getValue.internalId mustBe internalId
        uaCaptor.getValue.identifier mustBe identifier

        application.stop()
      }
    }
  }
}
