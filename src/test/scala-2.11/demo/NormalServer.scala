package demo

import java.nio.ByteBuffer

import lorance.rxscoket._
import lorance.rxscoket.session.{ConnectedSocket, ServerEntrance}
import rx.lang.scala.Observable

/**
  *
  */
object NormalServer extends App{
  val server = new ServerEntrance("localhost", 10002)
  val socket: Observable[ConnectedSocket] = server.listen

  socket.subscribe(s => println(s"Hi, Mike, someone connected - "))
  socket.subscribe(s => println(s"Hi, John, someone connected - "))

  val protoStream = socket.flatMap(_.startReading)

  protoStream subscribe {info =>
    println(s"get info from stream - uuid: ${info.uuid}; length: ${info.length}; load: ${new String(info.loaded.array())}")
  }

  val withContext = socket.flatMap{connection => connection.startReading.map( connection -> _)}

  withContext.subscribe{x =>
    val connection = x._1
    val info = x._2

    val load = new String(info.loaded.array())
    println(s"from - ${connection.addressPair.remote.toString} - info - uuid: ${info.uuid}; length: ${info.length}; load: $load")

    val response = s"Hi client, I'm get your info - $load"
    val protoType = 2.toByte //custom with your client
    connection.send(ByteBuffer.wrap(session.enCode(protoType, response)))
  }

  Thread.currentThread().join()
}