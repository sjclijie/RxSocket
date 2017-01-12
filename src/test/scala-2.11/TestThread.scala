import lorance.rxscoket.rxsocketLogger
/**
  * How about a wait(time) thread awake but the lock being used by other thread?
  * Yes
  */
object TestThread extends App {
  val lock = new AnyRef

  object Worker extends Thread {
    //the worker thread
    override def run(): Unit = {
      lock.synchronized{
        rxsocketLogger.log("sleeping")
        lock.wait(10000000)
        rxsocketLogger.log("waitThread awaked")
      }
    }

    //invoke at call thread
    override def start(): Unit = {
      rxsocketLogger.log("start")
      super.start()
    }
  }

  Worker.start()

  def awake() = {
    lock.synchronized{
      Thread.sleep(2000)
      rxsocketLogger.log("notify")
      lock.notify()
      Thread.sleep(2000)
      rxsocketLogger.log("main release lcok Lock")
    }
  }

  //notice awake method invoke before Worker start()
  Thread.sleep(1000)
  awake()
}

/**
  * Output
  * main:1484234888350 - start
    Thread-0:1484234888356 - sleeping
    main:1484234891363 - notify
    main:1484234893365 - main release lcok Lock
    Thread-0:1484234893366 - waitThread awaked
  */