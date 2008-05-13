package org.scalide


import javax.swing._
import java.awt.{Color, Font, Dimension, BorderLayout}
import scala.actors._
import Actor._
import org.scalide.utils._
import ScalideInterpreterMessages._

class ScalideFrame(private val p : Actor) extends JFrame {
  import BetterSwing._
  
  val panel = new ScalidePanel(p)
  
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
        panel process res
      }
    }
  }
  
  def process(res : InterpResult) {
    proc ! res
  }
  
  private def guiTask(task : => Unit ) {
    proc ! GUITask(() => task)
  }
  
  //On startup add the gui task into the processor
  guiTask {
    def mkMenuBar = {
      import ScalideGUIMessages._
      new MenuBar{
        new Menu("File") {
          "New" does {p ! NewFile()}
          "Open Scalapad..." does {p ! OpenFile()}
          ---
          "Save" does {p ! SaveFile()}
        }
        new Menu("Interpreter") {
          "Restart" does {p ! RestartInterpreter()}
        }	
      }
    }
    
    println("Making Frame " + Thread.currentThread)
    //Set up the frame
    setJMenuBar(mkMenuBar)
    setTitle("Scalide")
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setContentPane(new JScrollPane(panel))
    pack
    setVisible(true)
    //Have to set size after making the frame visible
    setSize(new Dimension(500, 700))    
  }
  
  
  private case class GUITask(fn : () => Unit)
  
}
