package org.scalide

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.Color
import java.awt.Font

class ScalidePanel extends JPanel {
  
  val editor = new JTextPane
  swingLater {
	  editor.setEditable(true)
	  editor.setForeground(Color.BLUE)
	  editor.setBackground(Color.GRAY.brighter.brighter)
	  editor.setFont(new Font("Consolas", 0,12))
  }
  
  def start {
    import java.awt.Dimension
    
    swingLater {
	    println(Thread.currentThread)
	
	    val innerEditor = new JTextPane
	    innerEditor.setFont(new Font("Consolas", 0, 12))
	    innerEditor.setBorder(BorderFactory.createMatteBorder(0,0,0,2,Color.BLUE))
	    
	    
	    editor.setCaretPosition(editor.getDocument.getLength)
	    editor.insertComponent(innerEditor)
	    

	    
	    innerEditor.grabFocus
    }
  }
}

