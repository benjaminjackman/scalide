package org.scalide.gui

import javax.swing._
import utils.BetterSwing._
class InfoDialog(title : String, text : String ) extends JDialog{
  val body = new JTextPane()
  swingLater {
    setSize(400,400)
    body.setContentType("text/html")
    body.setEditable(false)
    body.setText(text)
    add(body)
    setModal(true)
    pack
    setVisible(true)
  }
}
