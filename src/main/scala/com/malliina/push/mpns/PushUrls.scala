package com.malliina.push.mpns

import com.malliina.io.FileSet

/**
  *
  * @author mle
  */
object PushUrls extends FileSet[PushUrl]("push.json") {
  /**
    * The same device may open different push URLs at different points in time, however the old ones still work until the
    * channel is closed, it seems. We only accept one push URL per device however, otherwise the same push notification
    * is sent multiple times to the same device. Therefore we need another way of uniquely identifying a device, and that
    * is the tag.
    *
    * @param elem data sent by device
    * @return
    */
  override protected def id(elem: PushUrl): String = elem.tag

  /**
    * This method is unreliable but a possibly backwards-compatible workaround.
    *
    * @param url push url to remove
    */
  def removeURL(url: String) = get().find(_.url == url).foreach(elem => removeID(id(elem)))
}
