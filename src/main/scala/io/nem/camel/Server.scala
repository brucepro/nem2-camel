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

package io.nem.camel

import io.nem.camel.conf.{AccountsLoader, DefaultShutdownStrategyTimeoutReduced, ParserOptions}
import io.nem.camel.routes.{AccountSubscriptionRoute, HttpProxyRoute, HttpToWSToHttpRoute}
import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.impl.DefaultCamelContext
import org.apache.commons.cli.{DefaultParser, HelpFormatter}
import org.slf4j.{Logger, LoggerFactory}

object Server extends App {
  override def main(args: Array[String]): Unit = {
    val logger: Logger = LoggerFactory.getLogger(getClass)

    // Initialization
    val cmd = new DefaultParser().parse(ParserOptions.options, args)
    val serverUrl = cmd.getOptionValue("url", "http://localhost:3000")
    if (cmd.hasOption("-help")) {
      new HelpFormatter().printHelp("nem2-camel", ParserOptions.options)
      System.exit(0)
    }
    logger.info("ServerUrl is " + serverUrl)
    val accounts = if (cmd.hasOption("accountsFile")) {
      new AccountsLoader().loadFromFile(cmd.getOptionValue("accountsFile"))
    } else new AccountsLoader().loadDefault()

    // WebSockets
    val webSockets = new CatapultWebSockets(serverUrl)
    accounts.foreach(webSockets.subscribe)

    // Apache Camel
    logger.info("Initializing Apache Camel Server")
    val context = new DefaultCamelContext()
    context.addRoutes(AccountSubscriptionRoute(context, webSockets))
    context.addRoutes(HttpToWSToHttpRoute(context, webSockets, serverUrl))
    context.addRoutes(HttpProxyRoute(context, serverUrl))
    context.setAllowUseOriginalMessage(false)
    context.setShutdownStrategy(new DefaultShutdownStrategyTimeoutReduced())
    context.start()

    // Add grateful camel shutdown
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        context.stop()
      }
    })
  }
}
