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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import io.nem.camel.routes.processors.AccountSubscriptionProcessor
import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder

case class AccountSubscriptionRoute(camelContext: CamelContext,
                                    private val webSockets: CatapultWebSockets) extends RouteBuilder {
  private val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  override def configure(): Unit = {
    from("netty4-http:http://0.0.0.0:9000/camel/confirmedAdded")
      .log("subscribing ${header.account} into confirmedAdded channel")
      .process(new AccountSubscriptionProcessor(List("confirmedAdded"), webSockets))

    from("netty4-http:http://0.0.0.0:9000/camel/unconfirmedAdded")
      .log("subscribing ${header.account} into unconfirmedAdded channel")
      .process(new AccountSubscriptionProcessor(List("unconfirmedAdded"), webSockets))

    from("netty4-http:http://0.0.0.0:9000/camel/partialAdded")
      .log("subscribing ${header.account} into partialAdded channel")
      .process(new AccountSubscriptionProcessor(List("partialAdded"), webSockets))

    from("netty4-http:http://0.0.0.0:9000/camel/status")
      .log("subscribing ${header.account} into status channel")
      .process(new AccountSubscriptionProcessor(List("status"), webSockets))

    from("netty4-http:http://0.0.0.0:9000/camel/sync")
      .log("subscribing ${header.account} into unconfirmedAdded and status channel")
      .process(new AccountSubscriptionProcessor(List("status", "unconfirmedAdded"), webSockets))


    from("netty4-http:http://0.0.0.0:9000/camel/subscribers")
      .process((exchange) => {
        exchange.getIn().setBody(mapper.writeValueAsString(webSockets.accountSubscribers))
      })
  }
}
