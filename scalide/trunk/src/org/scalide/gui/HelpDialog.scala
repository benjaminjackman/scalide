package org.scalide.gui

import javax.swing._
class HelpDialog extends InfoDialog {
  setTitle("Scalide Help")
  body.setText(res.Messages.help)
}
