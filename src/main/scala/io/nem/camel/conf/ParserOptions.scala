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

import org.apache.commons.cli.Options

object ParserOptions {
  val options: Options = new Options()
  options.addOption("u", "url", true, "Server URL. Default http://localhost:3000")
  options.addOption("h", "help", false, "print this message")
  options.addOption("a", "accountsFile", true, "File that contains the accounts and the channels to subscribe them")
}
