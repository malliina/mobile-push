# mobile-push #

Send push notifications to mobile devices. Supports:

- Apple Push Notification service (APNs) using HTTP/2
- Apple Push Notification service using the legacy binary protocol
- Google Cloud Messaging (GCM)
- Amazon Device Messaging (ADM)
- Microsoft Push Notification Service (MPNS)

## Installation ##

```
libraryDependencies += "com.github.malliina" %% "mobile-push" % "1.5.0"
```

## Usage ##

To push notifications to iOS devices, you need to obtain a certificate for your app. To push notifications to Android
devices, you must first obtain API keys from the provider (Google or Amazon).

To receive notifications, mobile devices must first register with your notification server. Setting this up is beyond
the scope of this library; let's assume you already have all this.

### Apple Push Notification service, using HTTP/2 ###

    val certKeyStore: KeyStore = ???
    val certPass: String = ???
    val topic = APNSTopic("org.company.MyApp")
    val deviceToken: APNSToken = APNSToken.build("my_hex_device_token_here").get
    val message = APNSMessage.simple("Hey, sexy!")
    val request = APNSRequest.withTopic(topic, message)
    val client = APNSHttpClient(certKeyStore, certPass, isSandbox = true)
    val result: Future[Either[ErrorReason, APNSIdentifier]] = client.push(deviceToken, request)

### Apple Push Notification service, legacy ###

    val certKeyStore: KeyStore = ???
    val certPass: String = ???
    val deviceHexID: APNSToken = APNSToken.build("my_hex_device_token_here").get
    val client = new APNSClient(certKeyStore, certPass, isSandbox = true)
    val message = APNSMessage.simple("Hey, sexy!")
    val pushedNotification: Future[ApnsNotification] = client.push(deviceHexID, message)

### Google Cloud Messaging ###

    val gcmApiKey: String = ???
    val deviceRegistrationId: GCMToken = GCMToken("registration_id_here")
    val client = new GCMClient(gcmApiKey)
    val message = GCMMessage(Map("key" -> "value"))
    val response: Future[MappedGCMResponse] = client.push(deviceRegistrationId, message)

### Amazon Device Messaging ###

    val clientId: String = ???
    val clientSecret: String = ???
    val deviceID: ADMToken = ADMToken("adm_token_here")
    val client = new ADMClient(clientId, clientSecret)
    val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)
    val response: Future[Response] = client.push(deviceID, message)

### Microsoft Push Notification Service ###

    val deviceURL: MPNSToken = MPNSToken.build("my_device_url_here").get
    val client = new MPNSClient
    val message = ToastMessage("text1", "text2", deepLink = "/App/Xaml/DeepLinkPage.xaml?param=value", silent = true)
    val response: Future[Response] = client.push(deviceURL, message)
