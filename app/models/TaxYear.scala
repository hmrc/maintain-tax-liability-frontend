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

package models

import controllers.routes._
import pages._
import play.api.mvc.{JavascriptLiteral, PathBindable}

sealed trait TaxYear {
  val year: Int
  val messagePrefix: String
  override def toString: String = year.toString
  val page: QuestionPage[Boolean]
  def changeUrl: String
}

case object CYMinus4TaxYear extends TaxYear {
  override val year: Int = 4
  override val messagePrefix: String = "cyMinusFour"
  override val page: QuestionPage[Boolean] = CYMinusFourYesNoPage
  override def changeUrl: String = CYMinusFourYesNoController.onPageLoad().url
}
case object CYMinus3TaxYear extends TaxYear {
  override val year: Int = 3
  override val messagePrefix: String = "cyMinusThree"
  override val page: QuestionPage[Boolean] = CYMinusThreeYesNoPage
  override def changeUrl: String = CYMinusThreeYesNoController.onPageLoad().url
}
case object CYMinus2TaxYear extends TaxYear {
  override val year: Int = 2
  override val messagePrefix: String = "cyMinusTwo"
  override val page: QuestionPage[Boolean] = CYMinusTwoYesNoPage
  override def changeUrl: String = CYMinusTwoYesNoController.onPageLoad().url
}
case object CYMinus1TaxYear extends TaxYear {
  override val year: Int = 1
  override val messagePrefix: String = "cyMinusOne"
  override val page: QuestionPage[Boolean] = CYMinusOneYesNoPage
  override def changeUrl: String = CYMinusOneYesNoController.onPageLoad().url
}

object TaxYear {

  val taxYears: Seq[TaxYear] = Seq(CYMinus4TaxYear, CYMinus3TaxYear, CYMinus2TaxYear, CYMinus1TaxYear)

  implicit val jsLiteral: JavascriptLiteral[TaxYear] = (value: TaxYear) => value.toString

  implicit def pathBindable(implicit intBinder: PathBindable[Int]): PathBindable[TaxYear] = new PathBindable[TaxYear] {
    override def bind(key: String, value: String): Either[String, TaxYear] = {

      def taxYearFromId(id: Int): Option[TaxYear] = {
        id match {
          case CYMinus4TaxYear.year => Some(CYMinus4TaxYear)
          case CYMinus3TaxYear.year => Some(CYMinus3TaxYear)
          case CYMinus2TaxYear.year => Some(CYMinus2TaxYear)
          case CYMinus1TaxYear.year => Some(CYMinus1TaxYear)
          case _ => None
        }
      }

      for {
        id <- intBinder.bind(key, value).right
        taxYear <- taxYearFromId(id).toRight("Not a valid tax year").right
      } yield taxYear
    }

    override def unbind(key: String, value: TaxYear): String = value.toString.trim.toLowerCase
  }

}
