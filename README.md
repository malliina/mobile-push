# mobile-push #

Send push notifications to mobile devices. Supports:

- Google Cloud Messaging (GCM)
- Amazon Device Messaging (ADM)
- Microsoft Push Notification Service (MPNS)

## Installation ##

```
libraryDependencies += "com.github.malliina" %% "mobile-push" % "0.3.0"
```

## Usage ##

To push notifications to iOS devices, you need to obtain a certificate for your app. To push notifications to Android
devices, you must first obtain API keys from the provider (Google or Amazon).

To receive notifications, mobile devices must first register with your notification server. Setting this up is beyond
the scope of this library; let's assume you already have all this.

### Apple Push Notification Service ###

```
val certKeyStore: KeyStore = ???
val certPass: String = ???
val deviceHexID: String = ???
val client = new APNSClient(certKeyStore, certPass, isSandbox = true)
val message = APNSMessage.simple("Hey, sexy!")
val pushedNotification: Future[ApnsNotification] = client.push(deviceHexID, message)
```

### Google Cloud Messaging ###

```
val gcmApiKey: String = ???
val deviceRegistrationId: String = ???
val client = new GoogleMessaging(gcmApiKey)
val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)
val response: Future[Response] = client.send(deviceRegistrationId, message)
```

### Amazon Device Messaging ###

```
val clientId: String = ???
val clientSecret: String = ???
val deviceID: String = ???
val client = new AmazonMessaging(clientId, clientSecret)
val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)
val response: Future[Response] = client.send(deviceID, message)
```

### Microsoft Push Notification Service ###

```
val deviceURL: String = ???
val client = new MPNSClient
val message = ToastMessage("text1", "text2", deepLink = "/App/Xaml/DeepLinkPage.xaml?param=value", silent = true)
val response: Future[Response] = client.send(deviceURL, message)
```
