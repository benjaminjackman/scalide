package org.scalide

class Scalide(private val args : Array[String]) {
  import scala.actors.Actor
  import Actor._
  import processors.ScalaProcessor
  import org.scalide.utils.Props
  
  //Load the properties
  try {
    Props.loadProps("scalide.properties")
  } catch {
  case e : java.io.IOException =>
    System.err.println(e.toString)
  }
  
  //Set up the actor for relaying messages back and forth
  val p : Actor = actor {
    import ScalideGUIMessages._
    import ScalideInterpreterMessages._
      loop {
        receive {
        case msg : ScalideGUIMessage => 
          msg match {
          case NewFile() => 
            println(msg)
          case OpenFile() => 
            println(msg)
          case SaveFile() => 
            println(msg)
          case RestartInterpreter() => 
            interp.restart()
          case cmd : GUICommand => 
            interp.process(cmd)
          }
        case msg : ScalideInterpreterMessage => 
          msg match {
          case res : InterpResult =>
            frame.process(res)
          }
        case msg => println("Unhandled Message " + msg)
        }
      }
  }
  
  val interp = new ScalaProcessor(p)
  val frame = new ScalideFrame(p)
  
  
  
}
