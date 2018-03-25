package com.malliina.push

import com.malliina.http.OkClient
import com.malliina.push.Execution.cached

import scala.concurrent.Future

object AsyncHttp extends AsyncHttp

trait AsyncHttp {
  def withClient[U](code: OkClient => Future[U]): Future[U] =
    usingAsync(OkClient.default)(code)

  def usingAsync[T <: AutoCloseable, U](res: T)(code: T => Future[U]): Future[U] = {
    val result = code(res)
    result.onComplete(_ => res.close())
    result
  }
}
