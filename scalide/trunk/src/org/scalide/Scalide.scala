package org.scalide

class Scalide(private val args : Array[String]) {
  import scala.actors.Actor
  import Actor._
  import processors.ScalaProcessor
  import org.scalide.gui.ScalideFrame
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
    import core.UserMessages._
    import core.InterpreterMessages._
      loop {
        import javax.swing._
        import java.io.File
        def mkFileChooser(action : (JFileChooser) => Int)(fileHandler : File => Unit) = {
          import javax.swing.filechooser._
          import utils.BetterSwing._
          
          val fc = new JFileChooser
          swingLater {
            fc.addChoosableFileFilter(new FileFilter {
              override def accept(f : File) = {
                f.getName.endsWith(".scalapad")
              }
              override def getDescription = {
                "Scalapad files"
              }
            })
            if (action(fc) == JFileChooser.APPROVE_OPTION) {
              fileHandler(fc.getSelectedFile)
            }
          }
          fc
        }
        receive {
        case msg : UserMessage =>
          msg match {
          case NewFile() => 
            println(msg)
          case OpenFile() => 
            println(msg)
            actor {
              mkFileChooser(_.showOpenDialog(frame)) {
                f =>
                actor {
                  println("Loading " + f.getAbsolutePath)
                  
                  {try {
                    Some(scala.xml.XML.load(f.getAbsolutePath))
                  } catch {
                  case e=>
                    println("Poorly Formatted XML File" + e.toString)
                    None
                  }}.foreach {frame.load(_)}
                }
              }
            }
          case SaveFile() => 
            println(msg)
          case RestartInterpreter() => 
            interp.restart()
          case ShowAboutDialog() =>
            actor {
              new gui.AboutDialog
            }
          case ShowHelpDialog() =>
            actor {
              new gui.HelpDialog
            }
          case cmd : ProcessCell => 
            interp.process(cmd)
          case SaveData(data) =>
            actor {
              mkFileChooser(_.showSaveDialog(frame)) {
                f => 
                actor {
                  val fn = {
                    val fn = f.getAbsolutePath; 
                    if (fn.endsWith(".scalapad")) fn else fn + ".scalapad"
                  }
                  scala.xml.XML.saveFull(fn, data, "UTF-8", true, null)
                }
              }
            }
          }
        case msg : InterpreterMessage => 
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
