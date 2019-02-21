[![Build Status](https://travis-ci.org/malliina/mobile-push.svg?branch=master)](https://travis-ci.org/malliina/mobile-push)
[![Maven Central](https://img.shields.io/maven-central/v/com.malliina/mobile-push_2.12.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.malliina%22%20AND%20a%3A%22mobile-push_2.12%22)

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
libraryDependencies += "com.malliina" %% "mobile-push" % "@VERSION@"
```

## Usage

To push notifications to iOS devices, you need to obtain a certificate for your app. To push notifications to Android
devices, you must first obtain API keys from the provider (Google or Amazon).

To receive notifications, mobile devices must first register with your notification server. Setting this up is beyond
the scope of this library; let's assume you already have all this.

### Apple Push Notification service, using token authentication

```scala mdoc:code:../src/test/scala/tests/APNS2.scala:apns-token
42
```

### Apple Push Notification service, using certificate authentication

```scala mdoc:code:../src/test/scala/tests/APNS2.scala:apns-cert
42
```

### Apple Push Notification service, legacy binary protocol

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:fcm
42
```

### Firebase Cloud Messaging, legacy HTTP API

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:fcm
42
```

### Google Cloud Messaging

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:gcm
42
```

### Amazon Device Messaging

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:adm
42
```

### Windows Push Notification Services

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:wns
42
```

### Microsoft Push Notification Service

```scala mdoc:code:../src/test/scala/tests/CodeSamples.scala:mpns
42
```
