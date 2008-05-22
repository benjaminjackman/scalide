package org.scalide.utils

//This class helps fork sysout and
//syserr onto a listener as well as 
//back to the original
import java.io._
object ForkStream {
  def apply(outStream : PrintStream, redirFn : (PrintStream) => Unit,  listener : (String) => Unit) {
    import actors._
    import Actor._

    //Create the pipes we need
    val iOut = new PipedInputStream
    val pOut = new PipedOutputStream(iOut)
  
    //Redirect to the new standard out
    redirFn(new PrintStream(pOut, true))
  
    //Now use and actor loop to read the data
    actor {
      loop {
        iOut.read match {
        case -1=>
          exit
        case b=>
          val sb = new StringBuilder
          outStream.write(b)
          sb.append(b.asInstanceOf[Char])
          while (iOut.available > 0) {
            iOut.read match {
            case -1=>
              exit
            case b=>
              outStream.write(b)
              sb.append(b.asInstanceOf[Char])
            }
          }
          outStream.flush
          listener(sb.toString)
        }
      }
    }
  }	
}
