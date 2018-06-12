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

package io.nem.camel.routes

import io.nem.camel.models.{CosignaturePayload, Payload}
import io.nem.camel.routes.processors.{ExtractHashProcessor, ExtractParentHashProcessor, WaitForWSMessageProcessor}
import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.{CamelContext, LoggingLevel}

case class HttpToWSToHttpRoute(camelContext: CamelContext,
                               private val webSockets: CatapultWebSockets,
                               private val serverUrl: String
                          ) extends RouteBuilder(camelContext) {

  override def configure(): Unit = {
    from("netty4-http:http://0.0.0.0:9000/transaction/sync?httpMethodRestrict=PUT&throwExceptionOnFailure=false")
      .streamCaching()
      .unmarshal()
      .json(JsonLibrary.Jackson, classOf[Payload])
      .process(new ExtractHashProcessor(webSockets))
      .marshal()
      .json(JsonLibrary.Jackson, classOf[Payload])
      .log(LoggingLevel.INFO, "announce the transaction to network")
      .to(s"$serverUrl/transaction?bridgeEndpoint=true&throwExceptionOnFailure=false")
      .process(new WaitForWSMessageProcessor(webSockets))

    from("netty4-http:http://0.0.0.0:9000/transaction/partial/sync?httpMethodRestrict=PUT&throwExceptionOnFailure=false")
      .streamCaching()
      .unmarshal()
      .json(JsonLibrary.Jackson, classOf[Payload])
      .process(new ExtractHashProcessor(webSockets))
      .marshal()
      .json(JsonLibrary.Jackson, classOf[Payload])
      .log(LoggingLevel.INFO, "announce the partical transaction to network")
      .to(s"$serverUrl/transaction/partial?bridgeEndpoint=true&throwExceptionOnFailure=false")
      .process(new WaitForWSMessageProcessor(webSockets))

    from("netty4-http:http://0.0.0.0:9000/transaction/cosignature/sync?httpMethodRestrict=PUT&throwExceptionOnFailure=false")
      .streamCaching()
      .unmarshal()
      .json(JsonLibrary.Jackson, classOf[CosignaturePayload])
      .process(new ExtractParentHashProcessor(webSockets))
      .marshal()
      .json(JsonLibrary.Jackson, classOf[CosignaturePayload])
      .log(LoggingLevel.INFO, "announce the cosignature transaction to network")
      .to(s"$serverUrl/transaction/cosignature?bridgeEndpoint=true&throwExceptionOnFailure=false")
      .process(new WaitForWSMessageProcessor(webSockets))
  }
}
