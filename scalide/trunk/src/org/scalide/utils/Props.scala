package org.scalide.utils

object Props extends PropertiesParser {
  var props : Map[String, String] = Map()
  
  /* Throws FileNotFoundException if the file was not found
   * Throws IOException on other errors
   */
  def loadProps(file : String) {
    import scala.io.Source
    import scala.util.matching.Regex
    val regex = new Regex("""\s*(.*)=\s*(.*)\s*""", "key", "value")
    def parse(line : String) : Option[Pair[String,String]] = {
      line match { 
      case `regex`(key, value) => 
        Some((key,value)) 
      case _ => 
        None
      }
    }
    
    try {
      props = Map()
      for (val line <- Source.fromFile(file).getLines) {
        parse(line) match {
        case Some(entry) =>
          println("Added Prop " + entry)
          props = props + entry
        case None =>
        }
      }
    }
  }
}
