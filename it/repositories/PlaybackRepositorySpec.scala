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
import org.mongodb.scala.bson.BsonDocument
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PlaybackRepositorySpec extends AnyWordSpec with Matchers
  with ScalaFutures with OptionValues with MongoSupport with MongoSuite with BeforeAndAfterEach {


  override def beforeEach() = Await.result(repository.collection.deleteMany(BsonDocument()).toFuture(),Duration.Inf)

  lazy val repository: PlaybackRepositoryImpl = new PlaybackRepositoryImpl(mongoComponent, config)

  val identifier: String = "1234567890"
  val sessionId: String = "sessionId"
  val newId: String = s"$identifier-$sessionId"

  "a playback repository" should {

    "must return None when no cache exists" in {

        val internalId = "Int-328969d0-557e-4559-sdba-074d0597107e"

        repository.get(internalId, identifier, sessionId).futureValue mustBe None
    }

    "must return user answers when cache exists" in {

        val internalId = "Int-328969d0-557e-2559-96ba-074d0597107e"
        val identifier = "Testing"
        val sessionId = "Test"
        val newId = s"$internalId-$identifier-$sessionId"

        val userAnswers: UserAnswers = UserAnswers(internalId,identifier,sessionId,newId)

        val initial = repository.set(userAnswers).futureValue

        initial mustBe true

        val cachedAnswers = repository.get(internalId, identifier, sessionId).futureValue.value
        cachedAnswers.internalId mustBe internalId
        cachedAnswers.identifier mustBe identifier
        cachedAnswers.sessionId mustBe sessionId

    }

  }
}
