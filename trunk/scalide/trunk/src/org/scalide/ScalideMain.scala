package org.scalide

object ScalideMain  {
  def main(args : Array[String]) : Unit = {
    println("Starting Scalide")
    val scalide = new Scalide(args);
    scalide.start
  }
}
