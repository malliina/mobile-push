package tests

import com.malliina.http.{HttpClient, OkClient}
import munit.FunSuite

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class BaseSuite extends FunSuite {
  def await[T](f: Future[T]) = Await.result(f, 40.seconds)

  val http = FunFixture[HttpClient[Future]](
    opts => OkClient.default,
    teardown = _.close()
  )
}
