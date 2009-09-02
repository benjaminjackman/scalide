package scalide.gui

import javax.swing._
import utils.BetterSwing._
class InfoDialog(val title2 : String, val text : String ) extends JDialog{
  val body = new JTextPane()
  swingLater {
    setSize(400,400)
    body.setContentType("text/html")
    body.setEditable(false)
    body.setText(text)
    add(body)
    setTitle(title2)
    setModal(true)
    pack
    setVisible(true)
  }
}
