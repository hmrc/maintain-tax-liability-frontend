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

package utils

import base.SpecBase
import play.api.libs.json._
import utils.RichJson._

class RichJsonSpec extends SpecBase {

  "RichJsObject" when {

    "setObject" must {
      "set a value at a path" in {
        val obj = Json.obj()
        obj.setObject(__ \ "key", JsString("value")) mustBe JsSuccess(Json.obj("key" -> "value"))
      }

      "fail if result is not a JsObject" in {
        val obj = Json.obj()
        obj.setObject(__ \ "key", JsString("value")).isSuccess mustBe true
      }
    }

    "removeObject" must {
      "remove a value at a path" in {
        val obj = Json.obj("key" -> "value")
        obj.removeObject(__ \ "key") mustBe JsSuccess(Json.obj())
      }

      "fail for missing key" in {
        val obj = Json.obj()
        obj.removeObject(__ \ "missing").isError mustBe true
      }
    }
  }

  "RichJsValue" when {

    "set" must {
      "return error for empty path" in {
        JsString("test").set(JsPath, JsString("value")).isError mustBe true
      }

      "return error for recursive search" in {
        JsString("test").set(JsPath \\ "key", JsString("value")).isError mustBe true
      }

      "set a key on a JsObject" in {
        val obj = Json.obj("a" -> 1)
        obj.set(__ \ "b", JsNumber(2)) mustBe JsSuccess(Json.obj("a" -> 1, "b" -> 2))
      }

      "fail to set a key on a non-JsObject" in {
        JsString("test").set(__ \ "key", JsString("value")).isError mustBe true
      }

      "set an index on a JsArray" in {
        val arr = Json.arr("a", "b")
        arr.set(__(0), JsString("x")) mustBe JsSuccess(Json.arr("x", "b"))
      }

      "append to a JsArray when index equals length" in {
        val arr = Json.arr("a")
        arr.set(__(1), JsString("b")) mustBe JsSuccess(Json.arr("a", "b"))
      }

      "fail for out of bounds index" in {
        val arr = Json.arr("a")
        arr.set(__(5), JsString("b")).isError mustBe true
      }

      "fail to set an index on a non-JsArray" in {
        JsString("test").set(__(0), JsString("value")).isError mustBe true
      }

      "set a nested key path" in {
        val obj = Json.obj()
        obj.set(__ \ "a" \ "b", JsString("value")) mustBe JsSuccess(Json.obj("a" -> Json.obj("b" -> "value")))
      }

      "set a nested index path" in {
        val obj = Json.obj()
        obj.set(__ \ "a" \ "b" \ 0, JsString("value")) mustBe
          JsSuccess(Json.obj("a" -> Json.obj("b" -> Json.arr("value"))))
      }
    }

    "remove" must {
      "return error for empty path" in {
        Json.obj().remove(JsPath).isError mustBe true
      }

      "remove a key from a JsObject" in {
        val obj = Json.obj("a" -> 1, "b" -> 2)
        obj.remove(__ \ "a") mustBe JsSuccess(Json.obj("b" -> 2))
      }

      "fail to remove a missing key" in {
        Json.obj("a" -> 1).remove(__ \ "b").isError mustBe true
      }

      "fail to remove a key from a non-JsObject" in {
        JsString("test").remove(__ \ "key").isError mustBe true
      }

      "remove an index from a JsArray" in {
        val arr = Json.arr("a", "b", "c")
        arr.remove(__(1)) mustBe JsSuccess(Json.arr("a", "c"))
      }

      "fail for out of bounds index removal" in {
        Json.arr("a").remove(__(5)).isError mustBe true
      }

      "remove a nested path" in {
        val obj = Json.obj("a" -> Json.obj("b" -> 1, "c" -> 2))
        obj.remove(__ \ "a" \ "b") mustBe JsSuccess(Json.obj("a" -> Json.obj("c" -> 2)))
      }
    }
  }

}
