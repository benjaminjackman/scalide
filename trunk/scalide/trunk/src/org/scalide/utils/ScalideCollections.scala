package org.scalide.utils


object ScalideCollections {
  def justBefore[A](itr : Iterator[A], p : (A) => boolean) : Option[A] = {
    def justBeforeHelper(current : Option[A]) : Option[A]= {
      if (itr.hasNext) {
        val next = itr.next
        if (p(next)) {
          current
        } else {
          justBeforeHelper(Some(next))
        }
      } else {
        None
      }
    }
    justBeforeHelper(None)
  }
	
  def justAfter[A](itr : Iterator[A], p : (A) => boolean) : Option[A] = {
    def justAfterHelper(found : Boolean) : Option[A] = {
      if (itr.hasNext) {
        if (found) {
          Some(itr.next)
        } else {
          justAfterHelper(p(itr.next))
        }
      } else {
        None
      }
    }
    justAfterHelper(false)
  }
}
