package tests

import java.security.KeyStore

import com.mle.push.adm.ADMClient
import com.mle.push.android.AndroidMessage
import com.mle.push.apns.{APNSClient, APNSMessage}
import com.mle.push.gcm.{MappedGCMResponse, GCMMessage, GCMClient}
import com.mle.push.mpns.{MPNSClient, ToastMessage}
import com.ning.http.client.Response
import com.notnoop.apns.ApnsNotification
import org.scalatest.FunSuite

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
 * @author Michael
 */
class CodeSamples extends FunSuite {
//  test("Apple example") {
//    val certKeyStore: KeyStore = ???
//    val certPass: String = ???
//    val deviceHexID: String = ???
//    val client = new APNSClient(certKeyStore, certPass, isSandbox = true)
//    val message = APNSMessage.simple("Hey, sexy!")
//    val pushedNotification: Future[ApnsNotification] = client.push(deviceHexID, message)
//  }
//  test("Google example") {
//    val gcmApiKey: String = ???
//    val deviceRegistrationId: String = ???
//    val client = new GCMClient(gcmApiKey)
//    val message = GCMMessage(Map("key" -> "value"))
//    val response: Future[MappedGCMResponse] = client.push(deviceRegistrationId, message)
//  }
//  test("Amazon example") {
//    val clientId: String = ???
//    val clientSecret: String = ???
//    val deviceID: String = ???
//    val client = new ADMClient(clientId, clientSecret)
//    val message = AndroidMessage(Map("key" -> "value"), expiresAfter = 20.seconds)
//    val response: Future[Response] = client.push(deviceID, message)
//  }
//  test("MPNS example") {
//    val deviceURL: String = ???
//    val client = new MPNSClient
//    val message = ToastMessage("text1", "text2", deepLink = "/App/Xaml/DeepLinkPage.xaml?param=value", silent = true)
//    val response: Future[Response] = client.push(deviceURL, message)
//  }
}
