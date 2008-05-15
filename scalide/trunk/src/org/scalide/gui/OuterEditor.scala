package org.scalide.gui

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.{Color, Dimension, Font}
import java.awt.event.{KeyListener, KeyEvent}
import scala.actors._
import Actor._
import core.InterpreterMessages._
import java.io.{StringReader}

class OuterEditor(listener : Actor) extends JTextPane {
  
  var editors = List[InnerEditor](mkInnerEditor(false))
  var focused :Option[InnerEditor] = Some(editors.first)
  
  swingLater {
    println("Making Panel " + Thread.currentThread)
    setEditable(true)
    setForeground(Color.BLUE)
    setBackground(Color.GRAY.brighter.brighter)
    setFont(new Font("Consolas", 0,12))
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
    proc ! Refresh(editors)
  }
  
  def mkInnerEditor(isOut : Boolean) = {
    new InnerEditor(listener, isOut)
  }
  
  def process(res : InterpResult) {
    proc ! res
  }
  
  def start {
    proc ! Refresh
  }
  
  case class Refresh(editors : List[InnerEditor])

  
  val proc : Actor = actor {
    loop {
      receive {
      case res : InterpResult =>
        var foundIt = false;
        var insertedIt = false;
        def mkNew = {
          val newEd = mkInnerEditor(true)
          newEd.setText(res.text)
          newEd
        }
        editors = editors.flatMap{ ed =>
          if (insertedIt) {
            ed::Nil
          } else if (foundIt) {
            insertedIt = true
            if (ed.isOut) {
              println("Inserting result to out editor")
              ed.setText(res.text)
              ed::Nil
            } else {
              println("Making new editor")
              mkNew::ed::Nil
            }
          } else {
            println("Found Editor")
            if (ed.cell == res.cmd.cell) {
              foundIt = true
              }
            ed::Nil
          }
        }
        if (!insertedIt) {
          editors = editors:::mkNew::mkInnerEditor(false)::Nil
          focused = Some(editors.last)
        }
        println("Editors " + editors.size)
        proc ! Refresh(editors)
      case Refresh(editors) =>
        def refresh(editors : List[InnerEditor]) {
          swingLater {
            this.setText("")
            setCaretPosition(getDocument.getLength)
            for (ed <- editors) {
              val label = new JLabel(if (ed.isOut) "out " else "in ")
              label.setForeground(Color.BLUE)
              label.setFont(new Font("Consolas", 0, 10))
              insertComponent(label)
              insertComponent(ed)
              getEditorKit.read(new StringReader("\n"), getDocument(), getDocument().getLength())
            }
            //Focus on the active editor
            focused match {
            case Some(x) => x.grabFocus
            case None =>
            }
          }
        }
        refresh(editors)
      }
    }
  }
}

