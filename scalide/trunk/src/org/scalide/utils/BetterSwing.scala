package org.scalide.utils

import javax.swing._;
object BetterSwing {
  def swingLater(fn: => Unit) {
    SwingUtilities.invokeLater{
      new Runnable() {
        def run {
          fn
        }
      }
    }
  }
}
