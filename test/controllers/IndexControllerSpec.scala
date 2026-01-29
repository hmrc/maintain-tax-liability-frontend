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
import config.ErrorHandler
import connectors.{TrustsConnector, TrustsStoreConnector}
import generators.ModelGenerators
import models.TaskStatus.{Completed, InProgress, TaskStatus}
import models.{FirstTaxYearAvailable, UserAnswers}
import org.mockito.{ArgumentCaptor, Mockito}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.mvc.Results.InternalServerError
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach with ModelGenerators {

  lazy val indexRoute: String = routes.IndexController.onPageLoad(identifier).url

  val mockTrustsConnector: TrustsConnector           = Mockito.mock(classOf[TrustsConnector])
  val mockTrustsStoreConnector: TrustsStoreConnector = Mockito.mock(classOf[TrustsStoreConnector])
  val errorHandler: ErrorHandler                     = injector.instanceOf[ErrorHandler]

  override def beforeEach(): Unit = {
    reset(playbackRepository)
    reset(mockTrustsConnector)
    reset(mockTrustsStoreConnector)

    when(playbackRepository.set(any())).thenReturn(Future.successful(true))

    when(mockTrustsStoreConnector.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(mockTrustsStoreConnector.getTaskStatus(any())(any(), any()))
      .thenReturn(Future.successful(InProgress))
  }

  "IndexController" when {

    "there are no previously saved answers" when {

      "CY-4 and earlier years to declare" must {
        "set new user answers in repository and redirect to CYMinusFourEarlierYearsController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 4, earlierYearsToDeclare = true)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusFourEarlierYearsController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "CY-4 and no earlier years to declare" must {
        "set new user answers in repository and redirect to CYMinusFourYesNoController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 4, earlierYearsToDeclare = false)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusFourYesNoController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "CY-3 and earlier years to declare" must {
        "set new user answers in repository and redirect to CYMinusThreeEarlierYearsController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 3, earlierYearsToDeclare = true)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusThreeEarlierYearsController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "CY-3 and no earlier years to declare" must {
        "set new user answers in repository and redirect to CYMinusThreeYesNoController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 3, earlierYearsToDeclare = false)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusThreeYesNoController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "CY-2" must {
        "set new user answers in repository and redirect to CYMinusTwoYesNoController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 2, earlierYearsToDeclare = false)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusTwoYesNoController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "CY-1" must {
        "set new user answers in repository and redirect to CYMinusOneYesNoController" in {

          when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
            .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 1, earlierYearsToDeclare = false)))

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CYMinusOneYesNoController.onPageLoad().url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(playbackRepository).set(uaCaptor.capture)
          uaCaptor.getValue.internalId mustBe internalId
          uaCaptor.getValue.identifier mustBe identifier

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "unexpected result for number of years ago of first tax year available" must {
        "return internal server error" in
          forAll(arbitrary[Int].suchThat(x => x == 0 || x > 4)) { int =>
            beforeEach()

            when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
              .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = int, earlierYearsToDeclare = false)))

            val application = applicationBuilder(userAnswers = None)
              .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
              .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
              .build()

            val request = FakeRequest(GET, indexRoute)

            val result = route(application, request).value

            status(result) mustEqual INTERNAL_SERVER_ERROR

            contentAsString(result) mustEqual await(errorHandler.internalServerErrorTemplate(request)).toString()

            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(playbackRepository).set(uaCaptor.capture)
            uaCaptor.getValue.internalId mustBe internalId
            uaCaptor.getValue.identifier mustBe identifier

            verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

            application.stop()
          }
      }
    }

    "there are previously saved answers" when {

      "task is Completed" must {
        "redirect to CheckYourAnswersController" in {

          when(mockTrustsStoreConnector.getTaskStatus(any())(any(), any()))
            .thenReturn(Future.successful(Completed))

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
            .build()

          val request = FakeRequest(GET, indexRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe routes.CheckYourAnswersController.onPageLoad().url

          verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

          application.stop()
        }
      }

      "task is not completed" must {
        "not redirect to CheckYourAnswersController" in
          forAll(arbitrary[TaskStatus].suchThat(_ != Completed)) { taskStatus =>
            beforeEach()

            when(mockTrustsConnector.getFirstTaxYearToAskFor(any())(any(), any()))
              .thenReturn(Future.successful(FirstTaxYearAvailable(yearsAgo = 1, earlierYearsToDeclare = false)))

            when(mockTrustsStoreConnector.getTaskStatus(any())(any(), any()))
              .thenReturn(Future.successful(taskStatus))

            val userAnswers = emptyUserAnswers

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[TrustsConnector].toInstance(mockTrustsConnector))
              .overrides(bind[TrustsStoreConnector].toInstance(mockTrustsStoreConnector))
              .build()

            val request = FakeRequest(GET, indexRoute)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustNot be(routes.CheckYourAnswersController.onPageLoad().url)

            verify(playbackRepository).set(eqTo(userAnswers))
            verify(mockTrustsStoreConnector).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

            application.stop()
          }
      }
    }
  }

}
