/*
 * Copyright 2018 NEM
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

package io.nem.camel.conf

import com.github.tototoshi.csv.CSVReader
import io.nem.camel.models.AccountSubscriber

import scala.io.Source

class AccountsLoader {
  def loadFromFile(filename: String): List[AccountSubscriber] = {
    val csv = CSVReader.open(filename)
    extractAccountsFromCSV(csv)
  }

  private def extractAccountsFromCSV(csv: CSVReader): List[AccountSubscriber] = {
    csv.all().flatMap(row => {
      val account = row.head
      row.tail.map(channel => AccountSubscriber(account, channel))
    })
  }

  def loadDefault(): List[AccountSubscriber] = {
    val csv = CSVReader.open(Source.fromResource("accounts.csv"))
    extractAccountsFromCSV(csv)
  }
}

