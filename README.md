[![Build Status](https://github.com/malliina/mobile-push/workflows/Test/badge.svg)](https://github.com/malliina/mobile-push/actions)
[![Sponsored](https://img.shields.io/badge/chilicorn-sponsored-brightgreen.svg?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAA4AAAAPCAMAAADjyg5GAAABqlBMVEUAAAAzmTM3pEn%2FSTGhVSY4ZD43STdOXk5lSGAyhz41iz8xkz2HUCWFFhTFFRUzZDvbIB00Zzoyfj9zlHY0ZzmMfY0ydT0zjj92l3qjeR3dNSkoZp4ykEAzjT8ylUBlgj0yiT0ymECkwKjWqAyjuqcghpUykD%2BUQCKoQyAHb%2BgylkAyl0EynkEzmkA0mUA3mj86oUg7oUo8n0k%2FS%2Bw%2Fo0xBnE5BpU9Br0ZKo1ZLmFZOjEhesGljuzllqW50tH14aS14qm17mX9%2Bx4GAgUCEx02JySqOvpSXvI%2BYvp2orqmpzeGrQh%2Bsr6yssa2ttK6v0bKxMBy01bm4zLu5yry7yb29x77BzMPCxsLEzMXFxsXGx8fI3PLJ08vKysrKy8rL2s3MzczOH8LR0dHW19bX19fZ2dna2trc3Nzd3d3d3t3f39%2FgtZTg4ODi4uLj4%2BPlGxLl5eXm5ubnRzPn5%2Bfo6Ojp6enqfmzq6urr6%2Bvt7e3t7u3uDwvugwbu7u7v6Obv8fDz8%2FP09PT2igP29vb4%2BPj6y376%2Bu%2F7%2Bfv9%2Ff39%2Fv3%2BkAH%2FAwf%2FtwD%2F9wCyh1KfAAAAKXRSTlMABQ4VGykqLjVCTVNgdXuHj5Kaq62vt77ExNPX2%2Bju8vX6%2Bvr7%2FP7%2B%2FiiUMfUAAADTSURBVAjXBcFRTsIwHAfgX%2FtvOyjdYDUsRkFjTIwkPvjiOTyX9%2FAIJt7BF570BopEdHOOstHS%2BX0s439RGwnfuB5gSFOZAgDqjQOBivtGkCc7j%2B2e8XNzefWSu%2BsZUD1QfoTq0y6mZsUSvIkRoGYnHu6Yc63pDCjiSNE2kYLdCUAWVmK4zsxzO%2BQQFxNs5b479NHXopkbWX9U3PAwWAVSY%2FpZf1udQ7rfUpQ1CzurDPpwo16Ff2cMWjuFHX9qCV0Y0Ok4Jvh63IABUNnktl%2B6sgP%2BARIxSrT%2FMhLlAAAAAElFTkSuQmCC)](http://spiceprogram.org/oss-sponsorship)
[![Maven Central](https://img.shields.io/maven-central/v/com.malliina/mobile-push_3.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.malliina%22%20AND%20a%3A%22mobile-push_3%22)

# mobile-push

Send push notifications to mobile devices. Supports:

- Apple Push Notification service (APNs) using HTTP/2
- Apple Push Notification service using the legacy binary protocol
- Firebase Cloud Messaging (FCM) using the legacy HTTP API
- Google Cloud Messaging (GCM)
- Amazon Device Messaging (ADM)
- Windows Push Notification Services (WNS)
- Microsoft Push Notification Service (MPNS)

## Installation

```scala
libraryDependencies += "com.malliina" %% "mobile-push" % "3.8.2"
```

## Usage

To push notifications to iOS devices, you need to obtain a certificate for your app. To push notifications to Android
devices, you must first obtain API keys from the provider (Google or Amazon).

To receive notifications, mobile devices must first register with your notification server. Setting this up is beyond
the scope of this library; let's assume you already have all this.

### Apple Push Notification service, using token authentication


```scala
val conf = APNSTokenConf(
  Paths.get("path/to/downloaded-priv-key.p8"),
  KeyId("key_id_here"),
  TeamId("team_id_here")
)
val client = APNSTokenClient(conf, OkClient.default, isSandbox = true)
val topic = APNSTopic("org.company.MyApp")
val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").toOption.get
val message = APNSMessage.simple("Hey, sexy token!")
val request = APNSRequest.withTopic(topic, message)
val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
```

The above sample sends a simple message without any customizations. Explore the properties of
`APNSMessage` for more advanced messages. Here's a message with a text body and separate title:

```scala
val conf: APNSTokenConf = ???
val client = APNSTokenClient(conf, OkClient.default, isSandbox = true)
val topic = APNSTopic("org.company.MyApp")
val deviceToken = APNSToken.build("my_hex_device_token_here").toOption.get
val payload = APSPayload.full(AlertPayload("The Body", title = Option("Attention")))
val message = APNSMessage(payload)
val request = APNSRequest.withTopic(topic, message)
val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
```

### Apple Push Notification service, using certificate authentication

```scala
val certKeyStore: KeyStore = ???
val certPass: String = ???
val topic = APNSTopic("org.company.MyApp")
val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").toOption.get
val message = APNSMessage.simple("Hey, sexy!")
val request = APNSRequest.withTopic(topic, message)
val client = APNSHttpClient(certKeyStore, certPass, isSandbox = true)
val result: Future[Either[APNSError, APNSIdentifier]] = client.push(deviceToken, request)
```

### Firebase Cloud Messaging, legacy HTTP API

```scala
val gcmApiKey: String = ???
val deviceRegistrationId: GCMToken = GCMToken("registration_id_here")
val client = FCMLegacyClient(gcmApiKey, OkClient.default, executionContext)
val message = GCMMessage(Map("key" -> "value"))
val response: Future[MappedGCMResponse] = client.push(deviceRegistrationId, message)
```

### Amazon Device Messaging

```scala
val clientId: String = ???
val clientSecret: String = ???
val deviceID: ADMToken = ADMToken("adm_token_here")
val client = ADMClient(clientId, clientSecret, OkClient.default, executionContext)
val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)
val response: Future[HttpResponse] = client.push(deviceID, message)
```

### Windows Push Notification Services

```scala
val packageSid: String = ???
val clientSecret: String = ???
val credentials = WNSCredentials(packageSid, clientSecret)
val client = new WNSClient(credentials, OkClient.default)
val payload = ToastElement.text("Hello, world!")
val message = WNSMessage(payload)
val token = WNSToken.build("https://db5.notify.windows.com/?token=AwYAAABq7aWo").toOption.get
val response: Future[WNSResponse] = client.push(token, message)
```

### Microsoft Push Notification Service

```scala
val deviceURL: MPNSToken = MPNSToken.build("my_device_url_here").toOption.get
val client = new MPNSClient(OkClient.default, executionContext)
val message = ToastMessage("text1", "text2", deepLink = "/App/Xaml/DeepLinkPage.xaml?param=value", silent = true)
val response: Future[HttpResponse] = client.push(deviceURL, message)
```

## Releases

To publish a new version to Maven Central:

    sbt release
