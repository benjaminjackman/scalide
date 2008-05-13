package org.scalide.processors

import scala.actors.{InputChannel, OutputChannel}
import ScalideGUIMessages._
import ScalideInterpreterMessages._
import scala.actors.Actor
import Actor._
import java.io.{PrintWriter, PipedWriter, PipedReader}
class ScalaProcessor(private val p : Actor) {
  private var interp = mkInterpreter
  
  import scala.tools.nsc.{Interpreter, Settings}
  
  /**Processes the messages received from the 
   * interpreter and sends a message to the 
   * appropriate actor
   */
  private def handleError (res : String) {
    println("Err: [" + res + "]")
    commandProc ! ResultText(res)
  }
  
  private def handleResult (res : String) {
    println("Res: [" + res.trim + "]")
    commandProc ! ResultText(res.trim)    
  }
  
  private def mkPipe = {
    val pipe = new PipedWriter
    val writer = new PrintWriter(pipe)
    val reader = new PipedReader
    pipe.connect(reader)
    actor {
      loop {
        println("Awaiting Input")
        reader.read match {
        case -1 =>
          //We are done with this actor, we can exit it
          println("Pipe dead, exiting: 0")
          exit
        case x =>
          val sb = new StringBuilder
          val c = x.asInstanceOf[Char]
          println("Received: " + c)
          sb.append(c)
          while (reader.ready) {
            reader.read match {
            case -1 =>
              println("Pipe dead, exiting: 1")
              handleResult(sb.toString)
              exit
            case x =>
              val c = x.asInstanceOf[Char]
              println("Received: " + c)
              sb.append(c)
            }
          }
          handleResult(sb.toString)
        }
      }
    }
    writer
  }
  
  case class ResultText(res : String)
  case class Restart

  private def mkInterpreter = new Interpreter(new Settings(handleError _), mkPipe)
  
  private[scalide] def restart() {
     commandProc ! Restart
  }
  
  private[scalide] def process(cmd : GUICommand) {
    commandProc ! cmd
  }

  private val commandProc = actor {
    loop {
      def restart {
        interp = mkInterpreter
      }
      receive {
        case command : GUICommand => 
          interp.interpret(command.text)
          receive {
          case ResultText(text) =>
            p ! InterpResult(command, text)
          case Restart =>
            restart
          }
        case Restart =>
          restart
      }
    }
  }

}
