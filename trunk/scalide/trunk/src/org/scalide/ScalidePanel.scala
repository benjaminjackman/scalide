package org.scalide

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.{Color, Dimension, Font}
import scala.actors._
import Actor._
import ScalideInterpreterMessages._

class ScalidePanel(listener : Actor) extends JTextPane {
  
  
  
  swingLater {
    println("Making Panel " + Thread.currentThread)
    setEditable(true)
    setForeground(Color.BLUE)
    setBackground(Color.GRAY.brighter.brighter)
    setFont(new Font("Consolas", 0,12))

    val innerEditor = new JTextPane
    innerEditor.setFont(new Font("Consolas", 0, 12))
    innerEditor.setBorder(BorderFactory.createMatteBorder(0,1,1,2,Color.BLUE))
    
    setCaretPosition(getDocument.getLength)
    insertComponent(innerEditor)

    
    innerEditor.grabFocus
  }
  
  
  
  def process(res : InterpResult) {
    proc ! res
  }
  
  val proc : Actor = actor {
    
  }
}

