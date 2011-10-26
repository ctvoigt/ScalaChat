package oxnrtr.chat

import java.text.SimpleDateFormat

object Util {
  def validateIp(ipAddress: String): Boolean = {
    val octets = ipAddress.split("\\.")

    if (octets.length != 4) return false

    octets forall {
      octet =>
        val i = octet.toInt
        (i >= 0) && (i <= 255)
    }
  }

  def readIpAddress(msg: String): String = {
    var peer: String = null
    var validIp = false

    do {
      val input = Console.readLine(msg)
      validIp = validateIp(input)
      peer = input
    } while (!validIp)

    return peer
  }

  val sdf = new SimpleDateFormat("hh:mm:ss")
  def formatTime(timestamp: Long) = sdf.format(timestamp)

}