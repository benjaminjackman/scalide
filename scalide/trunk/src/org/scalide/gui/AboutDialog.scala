package org.scalide.gui

import javax.swing._
class AboutDialog extends InfoDialog {
  setTitle("Scalide About")
  body.setText(res.Messages.about)
}
