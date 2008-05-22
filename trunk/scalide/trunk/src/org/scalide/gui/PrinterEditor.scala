package org.scalide.gui

import javax.swing._
import core.UserMessages._
import utils.BetterSwing._
import java.io._
class PrinterEditor extends JTextPane {
  def display(s : String) {
    swingLater {
      setCaretPosition(getDocument.getLength)
      getEditorKit.read(new StringReader(s), getDocument(), getDocument().getLength())
    }
  }
  def process(msg : SysoutMessage) {
    display(msg.msg)
  }
  def process(msg : SyserrMessage) {
    display(msg.msg)
  }
}
