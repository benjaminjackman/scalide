package org.scalide

class Scalide(private val args : Array[String]) {
  import scala.actors.Actor._
  
  val p = actor {
    import ScalideGUIMessages._
      loop {
        receive {
        case msg : ScalideGUIMessage => msg match {
        case NewFile() => println(msg)
        case OpenFile() => println(msg)
        case SaveFile() => println(msg)
        case RestartInterpreter() => println(msg)
        case ProcessCommand(cmd) => println(cmd)    
        }	
        case msg => println("Unhandled Message" + msg)
        }
      }
  }
  
  p.start
  
  val frame = new ScalideFrame(p)
  
}
