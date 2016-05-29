
import java.nio.ByteBuffer

import lorance.rxscoket._
import lorance.rxscoket.session.{ConnectedSocket, ServerEntrance}
import rx.lang.scala.Observable

object DemoServerMain extends App {
  val server = new ServerEntrance("localhost", 10002)
  val socket: Observable[ConnectedSocket] = server.listen

  socket.subscribe(s => log(s"Hi, Mike, someone connected - "))
  socket.subscribe(s => log(s"Hi, John, someone connected - "))

  /**
    * read obs
    * NOTE: use `.publish` ensure the production of Observable is hot - a hot observable make a independence of a map chain,
    * it also important to avoid multi execute `startReading`.
    * Why use hot: It's really sad, the `map` make subscribe exec map body every times.so `stratReading` caused failure of
    */
  val read = socket.flatMap{l => l.startReading.map(l -> _)}.publish
  read.connect

  socket.subscribe(s => log(s"Hi, John2, someone connected - "))

  read.subscribe{r => r._2.foreach{x =>
      val clientInfo = new String(x.loaded.array())
      log(s"first subscriber get protocol - $clientInfo")

      val msg = "hi client~ I get your message - " + clientInfo
      log("response to client when get his msg - " + msg)
      r._1.send(ByteBuffer.wrap(session.enCode(0.toByte, msg)))
    }
  }

  read.subscribe{r => r._2.foreach{x => log(s"second subscriber get protocol - ${new String(x.loaded.array())}")}}

  Thread.currentThread().join()
}
