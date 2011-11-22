package oxnrtr.chat

import collection.mutable.{HashSet, ArrayBuffer}
import akka.actor.{ActorRef, Actor}
import Util._

object ChatCLI extends App {
  val localName = readLine("Enter name: ")
  val localIp = "127.0.0.1" //readIpAddress("Enter IP address: ")
  val localPort = readLine("Enter port number: ").toInt

  Actor.remote.start(localIp, localPort).register("chat", Actor.actorOf(ChatPeer(localName, localIp, localPort)))

  val myActor = Actor.remote.actorFor("chat", localIp, localPort)

  println("Special commands: `:log`, `:join`, `:invite`, `:peers`.")

  var abort = false
  while (!abort) {
    Thread.sleep(100)
    val input = Console.readLine("Send message: ")
    if (input.trim() == ":log") {
      val messages = (myActor ? GetChatLog).as[ArrayBuffer[Message]].get
      println("Chat log: " + messages.size)
      messages.foreach(println)
      println("----------------")
    } else if (input.trim() == ":peers") {
      val peers = (myActor ? GetPeers).as[HashSet[ActorRef]].get
      println("Connected peers: " + peers.size)
      peers.foreach(println)
      println("----------------")
    } else if (input.contains(":join")) {
      val args = input.substring(input.indexOf(":join")).split(" ")
      val remoteIpAddress = args(1)
      val remotePort = args(2).toInt
      val (peers, messages) = (myActor ? Join(remoteIpAddress, remotePort)).as[(collection.Set[ActorRef], Seq[Message])].get
      Actor.remote.actorFor("chat", remoteIpAddress, remotePort) ! Register(localIp, localPort)

      peers.foreach(peer =>
        if (peer != myActor) {
          peer ! Register(localIp, localPort)
          println(peer)
        }
      )
      messages.foreach(println)
    } else if (!input.trim().isEmpty) {
      myActor ! Send(input)
    }
  }
}