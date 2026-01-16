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

package controllers

import base.SpecBase
import models.{CYMinus3TaxYears, TaxYearRange}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.EarlierYearsToPayThanAskedYesNoView

class CYMinusThreeEarlierYearsControllerSpec extends SpecBase with BeforeAndAfterEach {

  val year = "2017"
  val mockTaxYearRange: TaxYearRange = Mockito.mock(classOf[TaxYearRange])

  lazy val cyMinusThreeEarlierYearsRoute: String = routes.CYMinusThreeEarlierYearsController.onPageLoad().url
  lazy val onSubmitRoute: Call = routes.CYMinusThreeEarlierYearsController.onSubmit()

  override def beforeEach(): Unit = {
    reset(mockTaxYearRange)
    when(mockTaxYearRange.yearAtStart(any())).thenReturn(year)
  }

  "CYMinusThreeEarlierYearsController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .build()

      val request = FakeRequest(GET, cyMinusThreeEarlierYearsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[EarlierYearsToPayThanAskedYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(year, onSubmitRoute)(request, messages).toString

      verify(mockTaxYearRange).yearAtStart(CYMinus3TaxYears)

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .overrides(bind[Navigator].toInstance(fakeNavigator))
        .build()

      val request = FakeRequest(POST, cyMinusThreeEarlierYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cyMinusThreeEarlierYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, cyMinusThreeEarlierYearsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

  }
}
