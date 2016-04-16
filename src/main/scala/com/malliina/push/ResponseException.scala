package com.malliina.push

import org.asynchttpclient.Response

class ResponseException(val response: Response) extends Exception("Request failed.")
