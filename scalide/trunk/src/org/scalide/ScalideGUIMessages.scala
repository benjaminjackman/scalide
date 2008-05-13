package org.scalide

object ScalideGUIMessages {
  sealed abstract class ScalideGUIMessage
  case class NewFile extends ScalideGUIMessage
  case class OpenFile extends ScalideGUIMessage
  case class SaveFile extends ScalideGUIMessage
  case class RestartInterpreter extends ScalideGUIMessage
  case class GUICommand(editor : InnerEditor, id : Int, text : String) extends ScalideGUIMessage
}