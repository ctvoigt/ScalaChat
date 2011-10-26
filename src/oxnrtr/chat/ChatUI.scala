package oxnrtr.chat

import collection.mutable.{HashSet, ArrayBuffer}
import akka.actor.{ActorRef, Actor}
import Util._

object ChatUI extends App {
  val localName = Console.readLine("Enter name: ")
  val localIp = readIpAddress("Enter IP adress: ")
  val localPort = readLine("Enter port number: ").toInt

//  val remoteActor = Actor.remote.start(localIp, localPort)
//  Actor.remote.register("chat", Actor.actorOf(new ChatPeer(localName, localIp, localPort)))

  Actor.remote.start(localIp, localPort).register("chat", Actor.actorOf(new ChatPeer(localName, localIp, localPort)))

  val myActor = Actor.remote.actorFor("chat", localIp, localPort)

  println("Special commands: `:log`, `:join`, `:peers`.")
  
  var abort = false
  while (!abort) {
    Thread.sleep(100)
    val input = Console.readLine("Send message: ")
    if (input.trim() == ":log")
      (myActor ? GetChatLog).as[ArrayBuffer[Message]].get.foreach(println)
    else if (input.trim() == ":peers")
      (myActor ? GetPeers).as[HashSet[ActorRef]].get.foreach(println)
    else if (input.contains(":join")) {
      val args = input.substring(input.indexOf(":join")).split(" ")
      val ip = args(1)
      val port = args(2).toInt
      val (peers, messages) = (myActor ? Join(ip, port)).as[(collection.Set[ActorRef], Seq[Message])].get
      peers.foreach(println)
      messages.foreach(println)
    } else {
      myActor ! Send(input)
    }
  }
}