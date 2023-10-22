package com.malliina.push.adm

import com.malliina.push.BaseSuite
import com.malliina.push.adm.{ADMClient, ADMToken}
import com.malliina.push.android.AndroidMessage

import scala.concurrent.duration.DurationInt

class ADMTests extends BaseSuite {
  http.test("send with adm".ignore) { httpClient =>
    val deviceID = ADMToken(
      "amzn1.adm-registration.v3.Y29tLmFtYXpvbi5EZXZpY2VNZXNzYWdpbmcuUmVnaXN0cmF0aW9uSWRFbmNyeXB0aW9uS2V5ITEhaXJTVmVVQXhJSnhnRGs3MGl0S0E1TExldGwxcWFPRzFHK3cwL1N1OS9zMnN1RFFBZHd1VkNWMXhaYlp3dTExWGdTNytOYk5jaVZ1OEtKWnoyNmhubnBoYTdiRVhlTzJTYkd3TlFaWXBNMHRCSDRnZi9KQUl1VGtoUzBwdjVkU2gwdGs1R2RWaVRRYzRHWFF3ZUU5MTU2MHI2ODRxN0pCYnJSMVFyaHhPUjI4NmVPT0lUcG5SWjJxUnRrOWZMdFdJdlpWMWxCVk1MSmtkRmIxY3llMkZZRWo0WFpiVWxMUEsrbnduZFB0Rm80TUdubFYxK1ZNdDA2bGJ5NFozNTZnbCtJVXBPRG9maTZ1NktnZXR0akZEeXhSV1pRV0lDRWh6b2ROenNxRTRsa0poS3EvSjJzaXFPQWpuVzd2Z0tINjZvRTFUV0MvOVJDaU81bE9pQmtGY3RnPT0hZGQ5WjZ2Z1c1MVJxY2kva2NmbmhyZz09"
    )
    val clientId = "amzn1.application-oa2-client.08957c08d1754dc2bf963d7c265f6c4b"
    val clientSecret = "65d6d9e6dffb7bc5e452fcfc84d5cb77e2ddc8f07592e944a2b2630d6653fdf4"
    val client = ADMClient(clientId, clientSecret, httpClient, munitExecutionContext)
    val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)

    val response = await(client.push(deviceID, message))
    assert(response.code == 200)
  }
}
