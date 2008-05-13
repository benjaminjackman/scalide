package org.scalide


object ScalideInterpreterMessages {
  import org.scalide.ScalideGUIMessages._
  sealed abstract class ScalideInterpreterMessage
  case class InterpResult(cmd : GUICommand, text : String) extends ScalideInterpreterMessage
}
