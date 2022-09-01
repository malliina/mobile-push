package com.malliina.push

import java.util.concurrent.{Executors, TimeUnit}

object Execution {
  private val service = Executors.newCachedThreadPool()

  def close(): Unit = {
    service.awaitTermination(1, TimeUnit.SECONDS)
    service.shutdown()
  }
}
