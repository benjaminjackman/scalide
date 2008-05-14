package org.scalide

import org.scalide.utils.BetterSwing._
import javax.swing._
import javax.swing.text._
import java.awt.{Color, Dimension, Font, Event}
import java.awt.event.{KeyEvent, ActionEvent}
import scala.actors._
import scalide.utils.BetterSwing._

class InnerEditor(private val listener : Actor, var isOut : Boolean) extends JTextPane {
  
  swingLater {
    
    
    //Binds all the actions that we want
    {
      val keymap = JTextComponent.addKeymap("InnerEditorBindings", getKeymap())
    
      //Util method for binding an action to a key
      def bindAction(key : Int, mask : Int) (act : => Unit) {
        val keystroke = KeyStroke.getKeyStroke(key, mask)
        val action = new AbstractAction {
          override def actionPerformed(e : ActionEvent) {
            act
          }
        }
        keymap.addActionForKeyStroke(keystroke, action)
      }
      
      import KeyEvent._;
      import Event._;
      
      //Bind the execute command
      bindAction(VK_ENTER, SHIFT_MASK) {
        import org.scalide.ScalideGUIMessages._
        
        val text = getText
        val command = GUICommand(this,1,text)
        println("Sending Command " + command)
        listener ! command
      }
      setKeymap(keymap)
      //Bind the other commands
    }
    
    setFont(new Font("Consolas", 0, 12))
    setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.BLUE))
    setTabStops(this, 4)
  }
}
