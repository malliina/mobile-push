package com.malliina.push

import com.ning.http.client.Response

/**
 * @author Michael
 */
class ResponseException(val response: Response) extends Exception("Request failed.")
