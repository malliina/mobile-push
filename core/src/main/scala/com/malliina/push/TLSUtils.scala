package com.malliina.push

import java.io.FileInputStream
import java.nio.file.Path
import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext}
import scala.util.Try

object TLSUtils {
  val jks = "JKS"

  def loadContext(file: Path, keyStorePass: String, storeType: String = jks): Try[SSLContext] =
    keyStoreFromFile(file, keyStorePass, storeType).map(ks => buildSSLContext(ks, keyStorePass))

  def keyStoreFromFile(file: Path, keyStorePass: String, storeType: String = jks): Try[KeyStore] =
    Try {
      val ks = KeyStore.getInstance(storeType)
      val keyStream = new FileInputStream(file.toFile)
      try {
        ks.load(keyStream, keyStorePass.toCharArray)
        ks
      } finally {
        keyStream.close()
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
