package org.scalide

import javax.swing._
class ScalideFrame {
  import java.awt.Color
  import java.awt.Font
  
  val frame = new JFrame;
  frame.setJMenuBar(mkMenuBar)
  val editor = new JTextPane
  editor.setEditable(false)
  editor.setForeground(Color.BLUE)
  editor.setBackground(Color.GRAY.brighter.brighter)
  editor.setFont(new Font("Consolas", 0,12))
  
  def mkMenuBar = {
    import org.scalide.utils.MenuBar
    new MenuBar{
      new Menu("File") {
        "New" does {}
        "Open Scalapad..." does {}
        ---
        "Save" does {}
      }
      new Menu("Interpreter") {
        "Restart" does {}
      }
    }
  }
  
  
  def start {
    import java.awt.Dimension
    
    val innerEditor = new JTextPane
    innerEditor.setFont(new Font("Consolas", 0, 12))
    innerEditor.setBorder(BorderFactory.createMatteBorder(0,0,0,2,Color.BLUE))
    
    
    editor.setCaretPosition(editor.getDocument.getLength)
    editor.insertComponent(innerEditor)
    
    //Set up the frame
    frame setTitle "Scalide"
    frame setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
    frame.getContentPane add editor
    frame.pack
    frame setVisible true
    //Have to set size after making the frame visible
    frame setSize new Dimension(500, 700)
    
    innerEditor.grabFocus
  }
}
