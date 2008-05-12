package org.scalide


sealed abstract class ScalideGUIMessage
object ScalideGUIMessages {
  case class NewFile extends ScalideGUIMessage
  case class OpenFile extends ScalideGUIMessage
  case class SaveFile extends ScalideGUIMessage
  case class RestartInterpreter extends ScalideGUIMessage
  case class ProcessCommand(cmd : String) extends ScalideGUIMessage
}

trait IScalideGUIProcessor {
  def !(msg : ScalideGUIMessage)
}
