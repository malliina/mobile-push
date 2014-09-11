package com.mle.push.android

import com.mle.io.FileSet
import com.mle.push.gcm.AndroidDevice

/**
 * @author Michael
 */
class AndroidDevices(fileName: String) extends FileSet[AndroidDevice](fileName) {
  override def id(elem: AndroidDevice): String = elem.id
}
