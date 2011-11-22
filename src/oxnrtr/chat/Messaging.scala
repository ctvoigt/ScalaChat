package oxnrtr.chat

import Util._

sealed trait Event

/**Join tells the peer that there is a new peer and requests a list of known peers and messages. */
case class Join(ipAddress: String, port: Int) extends Event

/**Join tells the peer that there is a new peer. */
case class Register(ipAddress: String, port: Int) extends Event

case class Leave(ipAddress: String, port: Int) extends Event

case object GetChatLog extends Event

case object GetPeers extends Event

case class Invite(ipAddress: String, port: Int) extends Event

case class ChatLog(log: Seq[Message]) extends Event

case class Peers(peers: Seq[ChatPeer]) extends Event

case class Send(message: String) extends Event

case class Message(sender: String, time: Long, message: String) extends Event {
  override def toString = "(" + formatTime(time) + ") " + sender + ": " + message
}


