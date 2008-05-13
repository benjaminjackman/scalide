package org.scalide.processors

import scala.actors.{InputChannel, OutputChannel}
import ScalideGUIMessages._
import ScalideInterpreterMessages._
import scala.actors.Actor
import Actor._
class ScalaProcessor(private val p : Actor) {
  private var interp = mkInterpreter
  
  import scala.tools.nsc.{Interpreter, Settings}
  
  /**Processes the messages received from the 
   * interpreter and sends a message to the 
   * appropriate actor
   */
  private def handleInterpreter (res : String) {
    commandProc ! ResultText(res)
  }
  
  case class ResultText(res : String)
  case class Restart

  private def mkInterpreter = new Interpreter(new Settings(handleInterpreter _))
  
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
