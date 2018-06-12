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

import io.nem.camel.websockets.CatapultWebSockets
import org.apache.camel.{AsyncCallback, AsyncProcessor, Exchange}
import org.slf4j.{Logger, LoggerFactory}


class WaitForWSMessageProcessor(private val webSockets: CatapultWebSockets) extends AsyncProcessor {

  val logger: Logger = LoggerFactory.getLogger(classOf[WaitForWSMessageProcessor])

  override def process(exchange: Exchange, callback: AsyncCallback): Boolean = {
    val httpStatus = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, classOf[Integer])
    logger.info(s"httpStatus=$httpStatus for ${exchange.getIn().getMessageId}")

    if (httpStatus > 299 || httpStatus < 200) {
      logger.info("error happened")
      true
    } else {
      val hash: String = exchange.getProperty("hash").asInstanceOf[String]
      logger.info("process for hash " + hash)
      webSockets.addCallbackForHash(hash, (message) => {
        exchange.getIn().setBody(message)
        callback.done(false)
      })
      if (webSockets.messageStorage.isComplete(hash)) {
        webSockets.messageStorage.callCallback(hash)
        webSockets.messageStorage.removeHashEntry(hash)
        true
      } else {
        false
      }
    }
  }

  override def process(exchange: Exchange): Unit = {
    val hash: String = exchange.getProperty("hash").asInstanceOf[String]
    webSockets.messageStorage.getMessageByHash(hash)
  }
}

