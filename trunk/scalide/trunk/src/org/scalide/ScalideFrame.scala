package org.scalide


import javax.swing._
import java.awt.{Color, Font, Dimension}
import scala.actors._
import org.scalide.utils._

class ScalideFrame(private val p : Actor) extends JFrame {
  import BetterSwing._
  
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
	
  val panel = new ScalidePanel
  
  swingLater {
    //Set up the frame
    setJMenuBar(mkMenuBar)
    setTitle("Scalide")
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    getContentPane.add(panel)
    pack
    setVisible(true)
    //Have to set size after making the frame visible
    setSize(new Dimension(500, 700))    
  }
}
