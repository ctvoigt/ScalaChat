package oxnrtr.chat

import collection.mutable.{ArrayBuffer, HashSet}
import akka.actor.{ActorRef, Actor}

case class ChatPeer(name: String, ipAddress: String, port: Int) extends Actor {
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

    case Join(remoteIpAddress, remotePort) =>
      self ! Register(remoteIpAddress, remotePort)
      val remoteActor = Actor.remote.actorFor("chat", remoteIpAddress, remotePort)
      val remotePeers = (remoteActor ? GetPeers).as[HashSet[ActorRef]].get
      val remoteMessages = (remoteActor ? GetChatLog).as[Seq[Message]].get
      self reply ((remotePeers, remoteMessages))

    case Invite(remoteIpAddress, remotePort) =>


    case Register(remoteIpAddress, remotePort) =>
      peers += Actor.remote.actorFor("chat", remoteIpAddress, remotePort)

    case Leave(remoteIpAddress, remotePort) =>
      peers -= Actor.remote.actorFor("chat", remoteIpAddress, remotePort)
  }
}



















