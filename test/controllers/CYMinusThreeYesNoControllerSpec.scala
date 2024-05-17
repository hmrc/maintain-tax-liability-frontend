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

package controllers

import base.SpecBase
import forms.YesNoFormProvider
import models.{CYMinus3TaxYears, TaxYearRange}
import navigation.Navigator
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.CYMinusThreeYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.CYMinusThreeYesNoView

class CYMinusThreeYesNoControllerSpec extends SpecBase with BeforeAndAfterEach {

  val taxYearDates: Seq[String] = Seq("6 April 2019", "5 April 2020")
  val mockTaxYearRange: TaxYearRange = Mockito.mock(classOf[TaxYearRange])

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix("cyMinusThree.liability", taxYearDates)

  lazy val cyMinusThreeYesNoRoute: String = routes.CYMinusThreeYesNoController.onPageLoad().url

  val validAnswer: Boolean = true

  override def beforeEach(): Unit = {
    reset(mockTaxYearRange)
    when(mockTaxYearRange.taxYearDates(any())(any())).thenReturn(taxYearDates)
  }

  "CYMinusThreeLiabilityController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .build()

      val request = FakeRequest(GET, cyMinusThreeYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CYMinusThreeYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, taxYearDates: _*)(request, messages).toString

      verify(mockTaxYearRange, times(2)).taxYearDates(eqTo(CYMinus3TaxYears))(any())

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CYMinusThreeYesNoPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .build()

      val request = FakeRequest(GET, cyMinusThreeYesNoRoute)

      val view = application.injector.instanceOf[CYMinusThreeYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), taxYearDates: _*)(request, messages).toString

      verify(mockTaxYearRange, times(2)).taxYearDates(eqTo(CYMinus3TaxYears))(any())

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .overrides(bind[Navigator].toInstance(fakeNavigator))
        .build()

      val request = FakeRequest(POST, cyMinusThreeYesNoRoute)
        .withFormUrlEncodedBody(("value", s"$validAnswer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      verify(mockTaxYearRange, times(1)).taxYearDates(eqTo(CYMinus3TaxYears))(any())

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TaxYearRange].toInstance(mockTaxYearRange))
        .build()

      val request = FakeRequest(POST, cyMinusThreeYesNoRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CYMinusThreeYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, taxYearDates: _*)(request, messages).toString

      verify(mockTaxYearRange, times(2)).taxYearDates(eqTo(CYMinus3TaxYears))(any())

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cyMinusThreeYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, cyMinusThreeYesNoRoute)
        .withFormUrlEncodedBody(("value", s"$validAnswer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

  }
}
