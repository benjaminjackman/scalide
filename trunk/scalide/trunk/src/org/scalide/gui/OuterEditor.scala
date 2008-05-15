package org.scalide.gui

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.{Color, Dimension, Font, BorderLayout}
import java.awt.event.{KeyListener, KeyEvent}
import scala.actors._
import Actor._
import core.InterpreterMessages._
import java.io.{StringReader}
import utils.{Props, ScalideCollections}
import ScalideCollections._

class OuterEditor(listener : Actor) extends JTextPane {

  class EditorGroup(val out : Boolean) extends JPanel {
    val editor = new CodeCellEditor(relay, out)
    val label = new JLabel(if (editor.isOut) "out " else "in ")
    label.setForeground(Color.BLUE)
    label.setFont(new Font(Props("InnerEditor.font.name", "Courier New"), 0, 10))
    label.setOpaque(false)
    setLayout(new BorderLayout)
    this.add(editor, BorderLayout.CENTER)
    this.add(label, BorderLayout.WEST)
 }

  
  val relay = actor {
    loop {
      react {
        case MOVE_FOCUS_UP()  =>
          proc ! MOVE_FOCUS_UP()
        case MOVE_FOCUS_DOWN() =>
          proc ! MOVE_FOCUS_DOWN()
        case x =>
          listener forward x
      }
    }
  }
  
  var editors = List[EditorGroup](new EditorGroup(false))
  var focused : Option[EditorGroup] = Some(editors.first)

  
  swingLater {
    println("Making Panel " + Thread.currentThread)
    setEditable(false)
    setForeground(Color.BLUE)
    setFont(new Font("Consolas", 0,12))
    /*
    addKeyListener(new KeyListener {
      def keyTyped(e : KeyEvent) {
        e.consume
      }
      def keyPressed(e : KeyEvent) {
        e.consume
      }
      def keyReleased(e : KeyEvent) {
        e.consume
      }
    })
     */
    proc ! Refresh(editors)
  }
  
  def process(res : InterpResult) {
    proc ! res
  }
  
  def start {
    proc ! Refresh
  }
  
  /* Generates a new code cell after the current one
   */
  
  def mkCodeCell {
    proc ! MakeCodeCell()
  }
  
  case class MakeCodeCell
  case class Refresh(editors : List[EditorGroup])

  
  val proc : Actor = actor {
    loop {
      receive {
      case MakeCodeCell() =>
        
      case MOVE_FOCUS_UP() =>
        if (focused.isDefined) {
          justBefore(editors.elements, focused.get ==).foreach {
            ed =>
            focused = Some(ed)
            swingLater{ed.editor.grabFocus}
          }
        }
      case MOVE_FOCUS_DOWN() =>
        if (focused.isDefined) {
          justAfter(editors.elements, focused.get ==).foreach {
            ed =>
            focused = Some(ed)
            swingLater{ed.editor.grabFocus}
          }
        }	   
      case res : InterpResult =>
        var foundIt = false;
        var insertedIt = false;
        def mkNew = {
          val ed = new EditorGroup(true)
          swingLater {ed.editor.setText(res.text)}
          ed
        }
        editors = editors.flatMap { 
          ed =>
          if (insertedIt) {
            ed::Nil
          } else if (foundIt) {
            insertedIt = true
            if (ed.editor.isOut) {
              println("Inserting result to out editor")
              swingLater{ed.editor.setText(res.text)}
              ed::Nil
            } else {
              println("Making new editor")
              mkNew::ed::Nil
            }
          } else {
            println("Found Editor")
            if (ed.editor.cell == res.cmd.cell) {
              foundIt = true
              }
            ed::Nil
          }
        }
        if (!insertedIt) {
          val newEd = new EditorGroup(false)
          editors = editors:::mkNew::newEd::Nil
          focused = Some(newEd)
        }
        println("Editors " + editors.size)
        proc ! Refresh(editors)
      case Refresh(editors) =>
        def refresh(editors : List[EditorGroup]) {
          swingLater {
            this.setText("")
            setCaretPosition(getDocument.getLength)
            for (ed <- editors) {
              insertComponent(ed)
              getEditorKit.read(new StringReader("\n"), getDocument(), getDocument().getLength())
            }
            //Focus on the active editor
            focused match {
            case Some(ed) => ed.editor.grabFocus
            case None =>
            }
          }
        }
        refresh(editors)
      }
    }
  }
}

