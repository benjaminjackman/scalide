package org.scalide.core

object UserMessages {
  sealed abstract class UserMessage
  case class NewFile extends UserMessage
  case class OpenFile extends UserMessage
  case class SaveFile extends UserMessage
  case class RestartInterpreter extends UserMessage
  case class ProcessCell(cell : CodeCell, requestId : Int, text : String) extends UserMessage
}