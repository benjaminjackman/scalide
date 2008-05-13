package org.scalide.utils

import javax.swing._;
object BetterSwing {
  def swingLater[R](fn: => R) {
    SwingUtilities.invokeLater{
      new Runnable() {
        def run {
          fn
        }
      }
    }
  }
}
