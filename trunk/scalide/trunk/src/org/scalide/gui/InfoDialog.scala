package org.scalide.gui

import javax.swing._
class InfoDialog extends JDialog{
  val body = new JTextArea
  setSize(400,400)
  add(body)
  setModal(true)
  setVisible(true)
}
