package org.scalide

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.{Color, Dimension, Font}
import scala.actors._
import Actor._
import ScalideInterpreterMessages._

class OuterEditor(listener : Actor) extends JTextPane {
  
  val innerEditor = new InnerEditor(listener)
  
  swingLater {
    println("Making Panel " + Thread.currentThread)
    setEditable(true)
    setForeground(Color.BLUE)
    setBackground(Color.GRAY.brighter.brighter)
    setFont(new Font("Consolas", 0,12))
    
    
    setCaretPosition(getDocument.getLength)
    insertComponent(innerEditor)
  }
  
  def process(res : InterpResult) {
    proc ! res
  }
  
  def start {
    innerEditor.grabFocus
  }
  
  val proc : Actor = actor {
    loop {
      receive {
      case res : InterpResult =>
               
      
      }
    }
  }
}

