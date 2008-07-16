package org.scalide2

class Scalide(private val args : Array[String]) {
  //TODO First things redirect the standard out and standard error, 
  //if someone gets printed there before we do this we will have problems
  
  //Read in the config file name if it exists
 import java.io.File
  val config : Option[File] = {
    if (args.size > 0) {
      val f = Some(new File(args(0)))
      if (f.get.exists) f else throw new IllegalArgumentException("Unable to read in config file, does not exist") 
    }else {
      None
    }
  }	
  
  //TODO add additional command line processing here
  
  //TODO Load in the config file here and read in any options
  
  
}
