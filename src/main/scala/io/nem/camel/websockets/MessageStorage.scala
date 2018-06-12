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

import org.slf4j.LoggerFactory

import scala.collection.mutable

class MessageStorage {
  private val messages: mutable.Map[String, MessageValue] = mutable.Map()
  private val logger = LoggerFactory.getLogger(classOf[MessageStorage])

  def newHashEntry(hash: String): Unit = {
    messages.put(hash, MessageValue(None, None))
  }

  def addMessage(hash: String, message: String): Unit = {
    messages.get(hash) match {
      case Some(messageValue) => messages.put(hash, messageValue.copy(message = Some(message)))
      case None => logger.info("Ignored addMessage for " + hash)
    }
  }

  def addCallback(hash: String, callback: (String) => Unit): Unit = {
    messages.get(hash) match {
      case Some(messageValue) => messages.put(hash, messageValue.copy(callback = Some(callback)))
      case None => logger.info("Ignored addCallback for " + hash)
    }
  }

  def isComplete(hash: String): Boolean = messages.get(hash) match {
    case Some(messageValue) => messageValue.message.isDefined && messageValue.callback.isDefined
    case None => false
  }

  def removeHashEntry(hash: String): Unit = {
    messages.remove(hash)
  }

  def callCallback(hash: String): Unit = {
    messages.get(hash) match {
      case Some(messageValue) => messageValue.callback.get.apply(messageValue.message.get)
      case None => logger.info("callCallback with hash " + hash + " ignored")
    }
  }

  def getMessageByHash(hash: String): String = {
    messages.get(hash) match {
      case Some(messageValue) => messageValue.message.get
      case None =>
        logger.info("getMessageByHash with hash " + hash + " ignored")
        ""
    }
  }
}

case class MessageValue(message: Option[String], callback: Option[(String) => Unit])