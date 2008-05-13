package org.scalide

import org.scalide.utils.BetterSwing._
import javax.swing._
import javax.swing.text._
import java.awt.{Color, Dimension, Font, Event}
import java.awt.event.{KeyEvent, ActionEvent}
import scala.actors._

class InnerEditor(listener : Actor) extends JTextPane {
  swingLater {
    
    //Binds all the actions that we want
    {
      val keymap = JTextComponent.addKeymap("InnerEditorBindings", getKeymap())
    
      //Util method for binding an action to a key
      def bindAction(key : Int, mask : Int) (act : => Unit) {
        println("Binding Action")
        val keystroke = KeyStroke.getKeyStroke(key, mask)
        val action = new AbstractAction {
          override def actionPerformed(e : ActionEvent) {
            println("Key Pressed")
            act
          }
        }
        keymap.addActionForKeyStroke(keystroke, action)
      }
      
      import KeyEvent._;
      import Event._;
      
      //Bind the execute command
//      bindAction(VK_ENTER, SHIFT_MASK) {
      bindAction(VK_B, CTRL_MASK) {
        import org.scalide.ScalideGUIMessages._
        
        val text = "5+5"
        val command = GUICommand(1,text)
        println("Sending Command " + command)
        listener ! GUICommand(1, text)
      }
      
      //Bind the other commands
    }
    
    setFont(new Font("Consolas", 0, 12))
    setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.BLUE))
  }
}
