package tests

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class BaseSuite extends munit.FunSuite {
  def await[T](f: Future[T]) = Await.result(f, 40.seconds)
}
