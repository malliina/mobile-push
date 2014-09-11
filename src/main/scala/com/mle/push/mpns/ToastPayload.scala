package com.mle.push.mpns

import scala.xml.{Elem, NodeSeq}

/**
 * Do not automatically format this file.
 *
 * @author Michael
 */
object ToastPayload {
  /**
   *
   * @param text1
   * @param text2
   * @param deepLink The page to go to in app. For example: /page1.xaml?value1=1234&amp;value2=9876
   * @return
   */
  def toastXml(text1: String, text2: String, deepLink: String, silent: Boolean): Elem = {
    val silenceElement = if (silent) <wp:Sound Silent="true"/> else NodeSeq.Empty
    // payloads must be on same line of xml, do not let formatting mess it up
    <wp:Notification xmlns:wp="WPNotification">
      <wp:Toast>
        <wp:Text1>{text1}</wp:Text1>
        <wp:Text2>{text2}</wp:Text2>
        <wp:Param>{deepLink}</wp:Param>
        {silenceElement}
      </wp:Toast>
    </wp:Notification>
  }
}
