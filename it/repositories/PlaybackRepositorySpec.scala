/*
 * Copyright 2022 HM Revenue & Customs
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

package repositories

import models.UserAnswers
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.must.Matchers

class PlaybackRepositorySpec extends AsyncFreeSpec with Matchers
  with ScalaFutures with OptionValues with MongoSuite {

  val identifier: String = "1234567890"
  val sessionId: String = "sessionId"

  "a playback repository" - {

    "must return None when no cache exists" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-4559-sdba-074d0597107e"

        val repository = app.injector.instanceOf[PlaybackRepository]
        repository.get(internalId, identifier, sessionId).futureValue mustBe None
    }

    "must return user answers when cache exists" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-2559-96ba-074d0597107e"

        val repository = app.injector.instanceOf[PlaybackRepository]

        val userAnswers = UserAnswers(internalId, identifier, sessionId)

        val initial = repository.set(userAnswers).futureValue

        initial mustBe true

        val cachedAnswers = repository.get(internalId, identifier, sessionId).futureValue.value
        cachedAnswers.internalId mustBe internalId
        cachedAnswers.identifier mustBe identifier
        cachedAnswers.sessionId mustBe sessionId
    }

    "must reset cache for an internalId" in assertMongoTest(application) {
      (app, _) =>

        val internalId = "Int-328969d0-557e-4559-96ba-0d4d0597107e"

        val repository = app.injector.instanceOf[PlaybackRepository]

        val userAnswers = UserAnswers(internalId, identifier, sessionId)

        repository.set(userAnswers).futureValue

        repository.get(internalId, identifier, sessionId).futureValue.isDefined mustBe true

        repository.resetCache(internalId, identifier, sessionId).futureValue

        repository.get(internalId, identifier, sessionId).futureValue.isDefined mustBe false
    }
  }
}
