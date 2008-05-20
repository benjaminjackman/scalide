package org.scalide.processors

import scala.actors.{InputChannel, OutputChannel}
import core.UserMessages._
import core.InterpreterMessages._
import scala.actors.Actor
import Actor._
import java.io.{PrintWriter, PipedWriter, PipedReader}
class ScalaProcessor(private val p : Actor) {

  import scala.tools.nsc.{Interpreter, Settings, InterpreterResults}
  
  case class ResultText(res : String)
  case class Restart
  
  private[scalide] def restart() {
     commandProc ! Restart
  }
  
  private[scalide] def process(cmd : ProcessCell) {
    commandProc ! cmd
  }

  //This actor processes all commands that come across
  //from the interpreter
  private val commandProc = actor {
    class InterpWrapper() {
      private val pipe = new PipedWriter
      private val writer = new PrintWriter(pipe)
      private val reader = new PipedReader
      private val interp = new Interpreter(new Settings(handleError _), writer)
      pipe.connect(reader)
      
      private def handleError(res : String) {
        //TODO handle errors in a more uniform fashion
        println("Error: " + res)
      }
      
      def interpret(command : String) : InterpreterResults.Result = interp.interpret(command)
      
      def close {
        interp.close
        reader.close
      }
      
      def hasResult : Boolean = {
        reader.ready
      }
      
      def getResult : String = { 
        reader.read match {
        case -1 =>
          //We are done with this actor, we can exit it
          println("Error: Pipe dead, exiting: 0")
          exit
        case x =>
          val sb = new StringBuilder
          val c = x.asInstanceOf[Char]
          sb.append(c)
          def readMore {
            if (reader.ready) {
              reader.read match {
              case -1 =>
                println("Error: Pipe dead, exiting: 1")
              case x =>
                val c = x.asInstanceOf[Char]
                sb.append(c)
              }
            }
            if (reader.ready) {
              readMore
            } else {
              //Hack to fix issues with how the interpreter 
              //flushes its outputs.
              Thread.sleep(50)
              if (reader.ready) {
                readMore
              }
            }
          }
          readMore
          sb.toString
        }
      }
    }
    
    var interp = new InterpWrapper
    
   	def restart {
        interp.close
        interp = new InterpWrapper
    }

    loop {
      receive {
        case command : ProcessCell =>
          import InterpreterResults._
          interp.interpret(command.text) match {
          case Success | Error =>
            def getResult : String = {
              if (interp.hasResult) {
                //Here return the result, less the line end
                interp.getResult.stripLineEnd
              } else {
                "<No Result>"
              }
            }
            p ! InterpResult(command, getResult)
          case Incomplete =>
            //Do Nothing!
            //Just let the interpreter keep waiting
            p ! InterpResult(command, "<Incomplete Expression>")
          }
        case Restart =>
          restart
      }
    }
  }
}
