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


class CodeCellEditor(private val outEd : OuterEditor,  var isOut : Boolean) extends JTextPane {
  
  val cell = new CodeCell
  
  def isOut_(isOut : Boolean) {
    this.isOut = isOut 
  }
  
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
      
      //Bind the save action
      bindAction(VK_S, CTRL_MASK) {
        outEd.save(false)
      }
      
      bindAction(VK_R, CTRL_MASK) {
        outEd.restart
      }
      
      bindAction(VK_F1, 0) {
        outEd.help
      }
      
      //Bind the execute command
      bindAction(VK_N, CTRL_MASK) {
        //Jump up one cell
        outEd.mkCodeCell
      }
      
      bindAction(VK_UP, ALT_MASK) {
        //Jump up one cell
        outEd.mvFocusUp
      }
      
      bindAction(VK_DOWN, ALT_MASK) {
        //Jump down one cell
        outEd.mvFocusDown
      }
      
      bindAction(VK_ENTER, SHIFT_MASK) {
        outEd.interpret
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
