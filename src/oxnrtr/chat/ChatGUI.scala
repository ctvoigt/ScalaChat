package oxnrtr.chat

import swing._
import event.{KeyPressed, Key}
import java.awt.{Color, Rectangle}
import oxnrtr.chat.Util._
import akka.actor.Actor

object ChatGUI extends SimpleSwingApplication {
  val localName = "soc_GUI"
  val localIp = "127.0.0.1"
  val localPort = 2222

  val remoteActor = Actor.remote.start(localIp, localPort)
  Actor.remote.register("chat", Actor.actorOf(ChatPeer(localName, localIp, localPort)))

  val myActor = Actor.remote.actorFor("chat", localIp, localPort)



  val chatArea = new TextArea() {
    editable = false
    background = Color.WHITE
  }
  val inputField = new TextField()

  def sendButton = Button("Send") {
    sendMessage
  }

  val top = {
    new MainFrame {
      title = "P2P-Chat"
      preferredSize = new Dimension(600, 400)
      resizable = false

      contents = new NullPanel {
        add(chatArea, new Rectangle(0, 0, 600, 370))
        add(inputField, new Rectangle(0, 374, 520, 26))
        add(sendButton, new Rectangle(520, 374, 80, 25))
      }
    }
  }

  def sendMessage = {
    val input = inputField.text
    if (!input.trim().isEmpty) {
      chatArea.append("me: " + input + "\n")
      inputField.text = ""
      inputField.requestFocusInWindow()
      myActor ! Send(input)
    }
  }

  listenTo(inputField.keys)
  reactions += {
    case KeyPressed(`inputField`, Key.Enter, _, _) =>
      sendMessage
  }
}