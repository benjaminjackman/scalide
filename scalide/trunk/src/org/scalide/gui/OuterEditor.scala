package org.scalide.gui

import javax.swing._
import org.scalide.utils.BetterSwing._
import java.awt.{Color, Dimension, Font, BorderLayout}
import java.awt.event.{KeyListener, KeyEvent, FocusListener, FocusEvent}
import scala.actors._
import Actor._
import core.InterpreterMessages._
import core.UserMessages._
import java.io.{StringReader}
import utils.{Props, ScalideCollections}
import ScalideCollections._

class OuterEditor(listener : Actor) extends JTextPane {
  
  val version = 5

  //An editor group consists of an editor and the code for it
  class EditorGroup(val isOut : Boolean, val text : String) extends JPanel {
    def this(isOut : Boolean) = this (isOut, "")
    val editor = new CodeCellEditor(OuterEditor.this, isOut)
    val label = new JLabel(if (editor.isOut) "out " else "in ")
    swingLater {
      editor.setText(text)
      label.setForeground(Color.BLUE)
      label.setFont(new Font(Props("InnerEditor.font.name", "Courier New"), 0, 10))
      label.setOpaque(false)
      setLayout(new BorderLayout)
      this.add(editor, BorderLayout.CENTER)
      this.add(label, BorderLayout.WEST)
      editor.addFocusListener { 
        new FocusListener {
          def focusLost(e : FocusEvent) {}
          def focusGained(e : FocusEvent) {
            proc ! ChangeFocus(Some(EditorGroup.this))
          }
        }
      } 
    }
 }

  
  swingLater {
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
    proc ! Refresh()
  }
  
  def process(res : InterpResult) {proc ! res}
  def process(cmd : ProcessCell) {listener ! cmd}
  def start {proc ! Refresh()}
  def save (promptForFile : boolean ){proc ! Save(promptForFile)}
  def help {listener ! ShowHelpDialog()}
  def restart {listener ! RestartInterpreter()}
  def mkCodeCell {proc ! MakeCodeCell()}
  def mvFocusUp {proc ! MoveFocusUp() }
  def mvFocusDown {proc ! MoveFocusDown() }
  def load(xml : scala.xml.Elem) {proc ! Load(xml)}
  def interpret {proc ! InterpretCurrent()}
  def interpretAll {proc ! InterpretAll()}
  
  case class InterpretCurrent
  case class InterpretAll
  case class MakeCodeCell
  case class Refresh
  case class Save(promptForFile : Boolean)
  case class Load(xml : scala.xml.Elem)
  
  class CodeCellAction
  case class MoveFocusUp extends CodeCellAction
  case class MoveFocusDown extends CodeCellAction
  case class ChangeFocus(ed : Option[EditorGroup])

  
  val proc : Actor = actor {
    var editors = List[EditorGroup](new EditorGroup(false))
    var focused : Option[EditorGroup] = Some(editors.first)
    var i = 0
    loop {
      def generateSaveXML = {
        (<Scalapad version={version.toString}>
         {for (ed <- editors) yield {
          (<CodeCell isOut={ed.editor.isOut.toString} isFocus={
            (focused match {
            case Some(x) => x==ed
            case None => false
            }).toString
          }>{ed.editor.getText}</CodeCell>)
        }}
        </Scalapad>)
      }
      def interpret(ed : EditorGroup) = {
        swingLater { 
          import core.UserMessages._
          val text = ed.editor.getText
          val command = ProcessCell(ed.editor.cell,1,text)
          process(command)
        } 
      }
      receive {
      case InterpretCurrent() =>
        focused foreach {interpret _}
      case InterpretAll() =>
        editors foreach {interpret _}
      case ChangeFocus(ed)=>
        focused = ed
      case Save(prompt) =>
        val s = generateSaveXML
        listener ! SaveData(s, prompt)
      case Load(xml) =>
        val newEds = xml\"CodeCell" map {
          cell=>
          //determine if this is an out cell, default to false when we have no elements
          val isOut : Boolean = (cell\"@isOut").foldRight(false){
            (x,y) =>
            try {x.text.toBoolean} catch {case e=>false}
          }
          val isFocus : Boolean = (cell\"@isFocus").foldRight(false){
            (x,y) =>
            try {x.text.toBoolean} catch {case e=>false}
          }
          val ed = new EditorGroup(isOut, cell.text)
          if (isFocus) {
            focused = Some(ed)
            swingLater{ed.grabFocus}
          }
          ed
        } toList match {
        case Nil =>
          //Do nothing when we parse an empty list
        case xs =>
          editors = xs
        }
        proc ! Refresh()
      case MakeCodeCell() =>
        val newEd = new EditorGroup(false);
        editors = focused match {
        case Some(ed) =>
          editors:::newEd::Nil
        case None =>
          editors:::newEd::Nil
        }
        focused = Some(newEd)
        swingLater{newEd.grabFocus}
        proc ! Refresh()
      case MoveFocusUp() =>
        if (focused.isDefined) {
          justBefore(editors.elements, focused.get ==).foreach {
            ed =>
            focused = Some(ed)
            swingLater{ed.editor.grabFocus}
          }
        }
      case MoveFocusDown() =>
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
            //Return just the editor after we have already inserted
            ed::Nil
          } else if (foundIt) {
            //Now we are going to insert the editor since 
            //we have just found the old one
            insertedIt = true
            if (ed.editor.isOut) {
              swingLater{ed.editor.setText(res.text)}
              ed::Nil
            } else {
              mkNew::ed::Nil
            }
          } else {
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
        //Be sure to refresh the view after we change the list
        proc ! Refresh()
      case Refresh() =>
        val eds = editors
        swingLater {
          //Clear this window
          this.setText("")
          //Set the caret to the end of the document
          setCaretPosition(getDocument.getLength)
          eds.foreach { 
            ed =>
            insertComponent(ed)
            getEditorKit.read(new StringReader("\n"), getDocument(), getDocument().getLength())
          }
          //Focus on the active editor
          focused.foreach(_.editor.grabFocus)
        }
      }
    }
  }
}

