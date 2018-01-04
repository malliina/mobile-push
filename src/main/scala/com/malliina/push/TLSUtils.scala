package com.malliina.push

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext}

import com.malliina.util.Util

import scala.util.Try

object TLSUtils {

  def loadContext(resource: InputStream, keyStorePass: String, storeType: String = "JKS"): Try[SSLContext] =
    keyStoreFromResource(resource, keyStorePass, storeType).map(ks => buildSSLContext(ks, keyStorePass))

  def keyStoreFromResource(resource: InputStream, keyStorePass: String, storeType: String): Try[KeyStore] = Try {
    val ks = KeyStore.getInstance(storeType)
    Util.using(resource) { keyStream =>
      ks.load(keyStream, keyStorePass.toCharArray)
      ks
    }
  }

  def buildSSLContext(keyStore: KeyStore, keyStorePass: String): SSLContext = {
    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    kmf.init(keyStore, keyStorePass.toCharArray)
    val keyManagers = kmf.getKeyManagers
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagers, null, null)
    sslContext
  }
}
