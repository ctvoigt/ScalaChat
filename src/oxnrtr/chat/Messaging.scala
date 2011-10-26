package oxnrtr.chat

import collection.mutable.{ArrayBuffer, HashSet}
import akka.actor.{ActorRef, Actor}
import Util._
import java.util.Date

sealed trait Event

/**Join tells the peer that there is a new peer and requests a list of known peers and messages. */
case class Join(ipAddress: String, port: Int) extends Event

/**Join tells the peer that there is a new peer. */
case class Register(ipAddress: String, port: Int) extends Event

case class Leave(ipAddress: String, port: Int) extends Event

case object GetChatLog extends Event

case object GetPeers extends Event

case class ChatLog(log: Seq[Message]) extends Event

case class Peers(peers: Seq[ChatPeer]) extends Event

case class Send(message: String) extends Event

case class Message(sender: String, time: Long, message: String) extends Event {
  override def toString = "(" + formatTime(time) + ") " + sender + ": " + message
}

class ChatPeer(val name: String, ipAddress: String, port: Int) extends Actor {
  val messages = ArrayBuffer[Message]()
  val peers = HashSet[ActorRef]()

  def receive = {
    case msg@Message(from, time, message) =>
      println(msg)
      messages += msg

    case GetChatLog =>
      self reply messages

    case GetPeers =>
      self reply peers

    case Send(message) =>
      val msg = Message(name, System.currentTimeMillis(), message)
      messages += msg
      peers.foreach(p => p ! msg)

    case Join(ipAddress, port) =>
      peers += Actor.remote.actorFor("chat", ipAddress, port)
      self reply ((peers, messages))

    case Register(ipAddress, port) =>
      peers += Actor.remote.actorFor("chat", ipAddress, port)

    case Leave(ipAddress, port) =>
      peers -= Actor.remote.actorFor("chat", ipAddress, port)

  }
}
