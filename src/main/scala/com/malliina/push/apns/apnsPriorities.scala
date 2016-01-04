package com.malliina.push.apns

import com.malliina.push.ValidatingCompanion

/**
  * @author mle
  */
abstract sealed class APNSPriority(val priority: Int)

object APNSPriority extends ValidatingCompanion[Int, APNSPriority] {
  override def build(input: Int): Option[APNSPriority] = input match {
    case APNSImmediately.priority => Option(APNSImmediately)
    case APNSConsiderate.priority => Option(APNSConsiderate)
    case _ => None
  }

  override def write(t: APNSPriority): Int = t.priority
}

case object APNSImmediately extends APNSPriority(10)

case object APNSConsiderate extends APNSPriority(5)
