package com.malliina.push

import java.util.concurrent.{Executors, TimeUnit}

import scala.concurrent.ExecutionContext

object Execution {
  private val service = Executors.newCachedThreadPool()
  //implicit val cached: ExecutionContext = ExecutionContext.fromExecutorService(service)

  def close(): Unit = {
    service.awaitTermination(1, TimeUnit.SECONDS)
    service.shutdown()
  }
}
