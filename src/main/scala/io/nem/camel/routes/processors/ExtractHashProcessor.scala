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

import io.nem.camel.models.{AccountSubscriber, Channels, CosignaturePayload, Payload}
import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.{Exchange, Processor}

class ExtractHashProcessor(private val websockets: CatapultWebSockets) extends Processor {
  override def process(exchange: Exchange): Unit = {
    val body = exchange.getIn().getBody(classOf[Payload])
    exchange.getIn().setHeader("Connection", "keep-alive")
    exchange.setProperty("hash", body.hash.toUpperCase)
    websockets.subscribe(AccountSubscriber(body.address, Channels.STATUS))
    websockets.subscribe(AccountSubscriber(body.address, Channels.UNCONFIRMED_ADDED))
    websockets.listenHash(body.hash.toUpperCase())
  }
}

class ExtractParentHashProcessor(private val websockets: CatapultWebSockets) extends Processor {
  override def process(exchange: Exchange): Unit = {
    val body = exchange.getIn().getBody(classOf[CosignaturePayload])
    exchange.getIn().setHeader("Connection", "keep-alive")
    exchange.setProperty("hash", body.parentHash.toUpperCase)
    websockets.listenHash(body.parentHash.toUpperCase())
  }
}