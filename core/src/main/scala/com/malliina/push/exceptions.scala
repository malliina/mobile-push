package com.malliina.push

import com.malliina.http.{FullUrl, HttpResponse}

class PushException(message: String) extends Exception(message)

class NotJsonException(val input: String) extends PushException(s"Not JSON: $input")

class JsonException(val input: String, val error: String)
  extends PushException(s"Parse error for input: $input, error: $error")

class ResponseException(val response: HttpResponse, val url: FullUrl)
  extends PushException(s"Invalid response code: ${response.code} from '$url'.") {
  def code: Int = response.code
}
