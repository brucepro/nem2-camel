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

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.{CamelContext, LoggingLevel}
import org.slf4j.LoggerFactory

case class HttpProxyRoute(camelContext: CamelContext, serverUrl: String) extends RouteBuilder(camelContext) {
  private val logger = LoggerFactory.getLogger(classOf[HttpProxyRoute])

  override def configure(): Unit = {
    from("netty4-http:http://0.0.0.0:9000?matchOnUriPrefix=true&throwExceptionOnFailure=false&bridgeEndpoint=true&exchangePattern=InOut")
      .log(LoggingLevel.INFO, "proxying non announce transaction call ${header.uri}")
      .to(s"netty4-http:$serverUrl?throwExceptionOnFailure=false&bridgeEndpoint=true")
  }
}
