package org.scalide

import org.scalide.utils.BetterSwing._
import javax.swing._
import java.awt.{Color, Dimension, Font}
import scala.actors._

class InnerEditor(listener : Actor) extends JTextPane {
  swingLater {
    setFont(new Font("Consolas", 0, 12))
    setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.BLUE))
  }
}
