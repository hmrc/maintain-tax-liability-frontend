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

package repositories

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import com.google.inject.ImplementedBy
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.ActiveSession
import org.bson.conversions.Bson
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model._
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActiveSessionRepositoryImpl @Inject() (
  val mongoComponent: MongoComponent,
  val config: AppConfig
)(implicit val ec: ExecutionContext)
    extends PlayMongoRepository[ActiveSession](
      collectionName = "session",
      mongoComponent = mongoComponent,
      domainFormat = Format(ActiveSession.reads, ActiveSession.writes),
      indexes = Seq(
        IndexModel(
          ascending("updatedAt"),
          IndexOptions()
            .unique(false)
            .name("session-updated-at-index")
            .expireAfter(config.cachettlSessionInSeconds, TimeUnit.SECONDS)
        ),
        IndexModel(
          ascending("identifier"),
          IndexOptions()
            .unique(false)
            .name("identifier-index")
        ),
        IndexModel(
          ascending("internalId"),
          IndexOptions()
            .unique(false)
            .name("internal-id-index")
        )
      ),
      replaceIndexes = config.dropIndexes
    )
    with ActiveSessionRepository {

  private def selector(internalId: String): Bson =
    Filters.eq("internalId", internalId)

  override def get(internalId: String): Future[Option[ActiveSession]] = {
    val modifier = Updates.set("updatedAt", LocalDateTime.now())

    val updateOption = new FindOneAndUpdateOptions()
      .upsert(false)

    collection.findOneAndUpdate(selector(internalId), modifier, updateOption).toFutureOption()
  }

  override def set(session: ActiveSession): Future[Boolean] = {

    val find          = selector(session.internalId)
    val updatedObject = session.copy(updatedAt = LocalDateTime.now)
    val options       = ReplaceOptions().upsert(true)

    collection.replaceOne(find, updatedObject, options).headOption().map(_.exists(_.wasAcknowledged()))
  }

}

@ImplementedBy(classOf[ActiveSessionRepositoryImpl])
trait ActiveSessionRepository {

  def get(internalId: String): Future[Option[ActiveSession]]

  def set(session: ActiveSession): Future[Boolean]
}
