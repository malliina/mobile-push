# mobile-push #

Send push notifications to mobile devices. Supports:

+   Google Cloud Messaging (GCM)
+   Amazon Device Messaging (ADM)
+   Microsoft Push Notification Service (MPNS)

## Installation ##

```
libraryDependencies += "com.github.malliina" %% "mobile-push" % "0.0.8"
```

## Usage ##

To send notifications to Android devices, you must first obtain API keys from the provider (Google or Amazon). To 
receive notifications, mobile devices must first register with your notification server. Setting that up is beyond the 
scope of this library.

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