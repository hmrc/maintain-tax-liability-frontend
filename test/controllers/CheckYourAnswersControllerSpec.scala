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
import connectors.{TrustsConnector, TrustsStoreConnector}
import models.{UserAnswers, YearReturn, YearsReturns}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.TaskCompleted
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.mapping.Mapper
import utils.print.PrintHelper
import viewmodels.{AnswerRow, AnswerSection}
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with BeforeAndAfterEach {

  lazy val checkYourAnswersRoute: String = routes.CheckYourAnswersController.onPageLoad().url

  val mockPrintHelper: PrintHelper = mock[PrintHelper]
  val mockMapper: Mapper = mock[Mapper]
  val mockTrustsConnector: TrustsConnector = mock[TrustsConnector]
  val mockTrustsStoreConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  val fakeAnswerSections = Seq(
    AnswerSection("Heading", Seq(AnswerRow("Label", HtmlFormat.escape("Answer"), "url")))
  )

  val fakeYearsReturns: YearsReturns = YearsReturns(List(
    YearReturn(taxReturnYear = "20", taxConsequence = true)
  ))

  override def beforeEach(): Unit = {
    reset(mockPrintHelper, mockMapper, playbackRepository, mockTrustsConnector, mockTrustsStoreConnector)

    when(mockPrintHelper(any())(any())).thenReturn(fakeAnswerSections)

    when(mockMapper(any())).thenReturn(fakeYearsReturns)

    when(playbackRepository.set(any())).thenReturn(Future.successful(true))

    when(mockTrustsConnector.setYearsReturns(any(), any())(any(), any())).thenReturn(okResponse)

    when(mockTrustsStoreConnector.setTaskComplete(any())(any(), any())).thenReturn(okResponse)
  }

  "CheckYourAnswersController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[PrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, checkYourAnswersRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeAnswerSections)(request, messages).toString

      application.stop()
    }

    "send mapped answers to backend and set task as complete" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[Mapper].toInstance(mockMapper),
          bind[TrustsConnector].toInstance(mockTrustsConnector),
          bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector)
        ).build()

      val controller = application.injector.instanceOf[CheckYourAnswersController]

      val request = FakeRequest(POST, checkYourAnswersRoute)

      val result = controller.onSubmit().apply(request)

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.maintainATrustOverviewUrl

      verify(mockMapper)(any())

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(playbackRepository).set(uaCaptor.capture)
      uaCaptor.getValue.get(TaskCompleted).get mustBe true

      verify(mockTrustsConnector).setYearsReturns(any(), eqTo(fakeYearsReturns))(any(), any())
      verify(mockTrustsStoreConnector).setTaskComplete(any())(any(), any())

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, checkYourAnswersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, checkYourAnswersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

  }
}
