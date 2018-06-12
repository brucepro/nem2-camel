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

package io.nem.camel.websockets

import java.net.URL

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.nem.camel.models.AccountSubscriber
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.ws.{WebSocket, WebSocketListener, WebSocketUpgradeHandler}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class CatapultWebSockets(private val serverUrl: String) {
  private val logger: Logger = LoggerFactory.getLogger(classOf[CatapultWebSockets])
  private val mapper = new ObjectMapper
  private val client = new DefaultAsyncHttpClient()

  var accountSubscribers: List[AccountSubscriber] = List()
  private var uidSubscriber: String = _
  val messageStorage = new MessageStorage()

  logger.info("Creating CatapultWebSockets")
  val url = new URL(serverUrl)
  val wsUrl: String = if (url.getProtocol.contains("s")) {
    "wss://"
  } else {
    "ws://"
  } + s"${url.getHost}:${url.getPort}/ws"
  private val webSockets: WebSocket = client.prepareGet(wsUrl)
    .execute(new WebSocketUpgradeHandler.Builder()
      .addWebSocketListener(new WebSocketListener {
        override def onTextFrame(payload: String, finalFragment: Boolean, rsv: Int): Unit = {
          logger.info("on message: " + payload)
          if (payload.contains("uid")) {
            val uidMessage = mapper.readValue(payload, classOf[UID])
            uidSubscriber = uidMessage.uid
          } else {
            processMessage(payload)
          }
        }

        override def onError(t: Throwable): Unit = {
          logger.error("error")
          t.printStackTrace()
        }

        override def onClose(websocket: WebSocket, code: Int, reason: String): Unit = {
          logger.info("\n\nWEBSOCKET CLOSED. code=" + code + ", reason=" + reason + "\n\n")
        }

        override def onOpen(websocket: WebSocket): Unit = {
          logger.info("opening websocket")
        }
      }).build()).get()

  def subscribe(accountSubscriber: AccountSubscriber): Unit = {
    if (!accountSubscribers.contains(accountSubscriber)) {
      if (uidSubscriber != null) {
        val account = accountSubscriber.account
        val channel = accountSubscriber.channel
        val subscribe = new Subscribe(uidSubscriber, s"$channel/$account")
        val subscribeBody = mapper.writeValueAsString(subscribe)
        logger.info("subscribeBody: " + subscribeBody)
        webSockets.sendTextFrame(subscribeBody)
        accountSubscribers = accountSubscriber :: accountSubscribers
      } else {
        Future {
          logger.info("uidSubscriber still null")
          Thread.sleep(1000)
        } onComplete {
          case Success(_) => subscribe(accountSubscriber)
          case Failure(e) => e.printStackTrace()
        }
      }
    }
  }

  def listenHash(hash: String): Unit = {
    messageStorage.newHashEntry(hash)
  }

  def addCallbackForHash(hash: String, callback: (String) => Unit): Unit = {
    messageStorage.addCallback(hash, callback)
  }

  private def processMessage(message: String): Unit = {
    val jsonObject = mapper.readTree(message)
    val errorHash = jsonObject.at("/hash").asText("")
    val successHash = jsonObject.at("/meta/hash").asText("")
    val hash = if (errorHash != "") {
      errorHash
    } else {
      successHash
    }
    messageStorage.addMessage(hash, message)
    if (messageStorage.isComplete(hash)) {
      messageStorage.callCallback(hash)
      messageStorage.removeHashEntry(hash)
    }
  }
}

class UID {
  @JsonProperty("uid") var uid: String = _
}

class Subscribe {

  def this(uid: String, subscribe: String) {
    this()
    this.uid = uid
    this.subscribe = subscribe
  }

  @JsonProperty("uid") var uid: String = _
  @JsonProperty("subscribe") var subscribe: String = _
}