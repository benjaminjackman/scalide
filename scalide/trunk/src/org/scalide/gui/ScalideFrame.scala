package org.scalide.gui


import javax.swing._
import java.awt.{Color, Font, Dimension, BorderLayout}
import scala.actors._
import Actor._
import org.scalide.utils._
import core.InterpreterMessages._

class ScalideFrame(private val p : Actor) extends JFrame {
  import BetterSwing._
  
  val editor = new OuterEditor(p)
  
  private[scalide] val proc = actor {
    
    loop {
      receive {
      case GUITask(fn) =>
        SwingUtilities.invokeLater(
          new Runnable() {
            def run {
              println(Thread.currentThread)
              fn()
            }
          }
        )
      case res : InterpResult =>
        editor process res
      }
    }
  }
  
  def process(res : InterpResult) {proc ! res}
  def load(data : scala.xml.Elem) {editor.load(data)}
  
  private def guiTask(task : => Unit ) {
    proc ! GUITask(() => task)
  }
  
  //On startup add the gui task into the processor
  guiTask {
    def mkMenuBar = {
      import core.UserMessages._
      val mb = new MenuBar {
        new Menu("File") {
          "New" does {p ! NewFile()}
          "Open Scalapad..." does {p ! OpenFile()}
          ---
          "Save Scalapad            Ctrl+S" does {editor.save(false)}
          "Save As Scalapad..." does {editor.save(true)}
          ---
          "Import Script..."
          ---
          "Export Script" does {}
          "Export As Script..." does {}
        }
        new Menu("Interpreter") {
          "Restart                  Ctrl+R" does {p ! RestartInterpreter()}
        }	
        new Menu("CodeCell") {
          "New                      Ctrl+N" does {editor.mkCodeCell}
        }	
        new Menu("Help") {
          "Contents                 F1" does {p ! ShowHelpDialog()}
          ---
          "About" does {p ! ShowAboutDialog()}
        }	
      }
      mb.setFont(new Font(Props("InnerEditor.font.name", "Courier New"), 0, 10))
      mb
    }
    
    println("Making Frame " + Thread.currentThread)
    //Set up the frame
    setJMenuBar(mkMenuBar)
    setTitle("Scalide")
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setContentPane(new JScrollPane(editor))
    pack
    setVisible(true)
    //Have to set size after making the frame visible
    setSize(new Dimension(500, 700))
    editor.start
  }
  
  private case class GUITask(fn : () => Unit)
  
}
