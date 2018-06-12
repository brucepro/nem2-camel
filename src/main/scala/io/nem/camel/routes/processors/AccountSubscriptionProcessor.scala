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

package io.nem.camel.routes.processors

import io.nem.camel.models.AccountSubscriber
import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.{Exchange, Processor}

class AccountSubscriptionProcessor(channels: List[String], websockets: CatapultWebSockets) extends Processor {
  override def process(exchange: Exchange): Unit = {
    if (exchange.getIn().getHeader("account") == null) {
      exchange.getIn().setBody("{\"message\": \"account is not defined\"}")
      exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400)
    } else {
      val account = exchange.getIn().getHeader("account").toString
      channels.foreach(channel => websockets.subscribe(AccountSubscriber(account, channel)))
      exchange.getIn().setBody("{\"status\": \"ok\"}")
    }
  }
}


