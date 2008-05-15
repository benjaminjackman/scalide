package org.scalide.gui

import scalide.utils.BetterSwing._
import javax.swing._
import javax.swing.text._
import java.awt.{Color, Dimension, Font, Event}
import java.awt.event.{KeyEvent, ActionEvent, KeyListener}
import scala.actors._
import scalide.utils.BetterSwing._
import scalide.utils.Props
import scalide.core._

class CodeCellEditorAction
case class MOVE_FOCUS_UP extends CodeCellEditorAction
case class MOVE_FOCUS_DOWN extends CodeCellEditorAction

class CodeCellEditor(private val listener : Actor, var isOut : Boolean) extends JTextPane {
  
  val cell = new CodeCell
  
  
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
      bindAction(VK_UP, ALT_MASK) {
        //Jump up one cell
        listener ! MOVE_FOCUS_UP()
      }
      
      bindAction(VK_DOWN, ALT_MASK) {
        //Jump down one cell
        listener ! MOVE_FOCUS_DOWN()
      }
      
      bindAction(VK_ENTER, SHIFT_MASK) {
        import core.UserMessages._
        
        val text = getText
        val command = ProcessCell(cell,1,text)
        println("Sending Command " + command)
        listener ! command
      }
      setKeymap(keymap)
      //Bind the other commands
    }
    
    addKeyListener(new KeyListener {
      import KeyEvent._;
      import Event._;

      def keyTyped(e : KeyEvent) {
      }
      def keyPressed(e : KeyEvent) {
      }
      def keyReleased(e : KeyEvent) {
      }
    })
    
    setFont(new Font(Props("InnerEditor.font.name", "Courier New"), 0, Props("InnerEditor.font.size", 12)))
    setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.BLUE))
    setTabStops(this, 4)
  }
}
