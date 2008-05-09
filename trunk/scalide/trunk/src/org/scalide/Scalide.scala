package org.scalide

class Scalide(private val args : Array[String]) {
  def start() {
    val frame = new ScalideFrame
    frame.start
  }
}
