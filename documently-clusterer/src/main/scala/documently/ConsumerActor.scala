package documently

import akka.actor.{ActorLogging, Actor}
import com.rabbitmq.client.{AMQP, Envelope, DefaultConsumer, ConnectionFactory}
import config.DocumentlySettings


/**
 * Copyright 2012 Amir Moulavi (amir.moulavi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Amir Moulavi
 */

class ConsumerActor extends Actor with ActorLogging {

  val namespace = "Documently.Messages.DocMetaEvents"
  val exchangeName = namespace + ":" + "DocumentUploaded"

  val documentlySettings = new DocumentlySettings()

  val factory = new ConnectionFactory()
  factory.setHost(documentlySettings.host)
  factory.setUsername(documentlySettings.username)
  factory.setPassword(documentlySettings.password)
  factory.setPort(documentlySettings.port)
  factory.setVirtualHost(documentlySettings.vhost)

  val connection = factory.newConnection()
  val channel = connection.createChannel()

  def receive = {
    case Start() => setupRabbitMQ()
  }

  private def setupRabbitMQ() {
    channel.exchangeDeclare(exchangeName, "fanout", true)
    val queueName = channel.queueDeclare().getQueue
    channel.queueBind(queueName, exchangeName, "")

    channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag:String, envelope:Envelope, properties:AMQP.BasicProperties, body:Array[Byte]) {
        println("Consumer Tag: %s".format(consumerTag))
        println("Delivery Tag: %s".format(envelope.getDeliveryTag))
        println("AppId: %s".format(properties.getAppId))
        println("ClassName: %s".format(properties.getClassName))
        println("CorrelationId: %s".format(properties.getCorrelationId))
        println("MessageId: %s".format(properties.getMessageId))
        println("Body: %s".format(body.toString))
      }
    })

  }

  override def postStop() {
    channel.close()
    connection.close()
  }

}