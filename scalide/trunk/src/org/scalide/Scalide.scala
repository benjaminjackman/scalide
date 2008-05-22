package org.scalide

class Scalide(private val args : Array[String]) {
  import core.UserMessages._
  utils.ForkStream(System.out, System.setOut, {x => p ! SysoutMessage(x)})
  utils.ForkStream(System.err, System.setErr, {x => p ! SyserrMessage(x)})
  
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
  
  case class SetCurrentSaveName(filename : Option[String])
  
  //Set up the actor for relaying messages back and forth
  lazy val p : Actor = actor {
    //Redirect the streams

    var currentSaveName : Option[String] = None
    
    import core.InterpreterMessages._
      
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
    def loadFile(filename : String) {
      {
        println("Loading [" + filename +"]");
        try {
          val data = Some(scala.xml.XML.load(filename))
          p ! SetCurrentSaveName(Some(filename))
          data
        } catch {
        case e =>
          println("Unable to read scalabook file [" + filename + "] " + e.toString)
          None
        }
      }.foreach { frame load _}
    }
    loop {
      receive {
      case SetCurrentSaveName(filename) =>
        currentSaveName = filename
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
                loadFile(f.getAbsolutePath)
              }
            }
          }
        case LoadFileByName(filename) =>
          actor {
            loadFile(filename)
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
        case x: SysoutMessage =>
          frame.process(x)
        case x : SyserrMessage =>
          frame.process(x)
        case cmd : ProcessCell => 
          interp.process(cmd)
        case SaveData(data, prompt) =>
          def save(filename : String) {
            try {
              scala.xml.XML.saveFull(filename, data, "UTF-8", true, null)
              p ! SetCurrentSaveName(Some(filename))
            } catch {
            case e=>
            }
          }
          if (currentSaveName.isEmpty || prompt) {
            actor {
              mkFileChooser(_.showSaveDialog(frame)) {
                f => 
                actor {
                  val fn = {
                    val fn = f.getAbsolutePath; 
                    if (fn.endsWith(".scalapad")) fn else fn + ".scalapad"
                  }
                  save(fn)
                }
              }
            }
          } else {
            val filename = currentSaveName.get 
            actor {
              save(filename)
            }
          }
        }
      case msg : InterpreterMessage => 
        msg match {
        case res : InterpResult =>
          frame.process(res)
        }
      case 
        msg => println("Unhandled Message " + msg)
      }
      
    }
  }	
  
  val interp = new ScalaProcessor(p)
  val frame = new ScalideFrame(p)
  
  if (args.size > 0) {
    p ! LoadFileByName(args(0))
  }
}
