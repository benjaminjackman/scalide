package org.scalide.misc

/** Just experimenting pay no heed to this class
 */
object ParsingExpressionGrammar {
  
  implicit def any2Nonterminal(a : Symbol) : NonTerminal = {
    NonTerminal(a);
  }
  
  case class NonTerminal(val a : Symbol) {
    def andThen (xs : NonTerminal*) = {
      ParsingExpression(a::xs.toList)
    }
    def is (o : NonTerminal*) = {
      ParsingExpression(this::o.toList)
    }
  }
  
  case class ParsingExpression(val nonTerminals : List[NonTerminal]) {
    
  }
  
}
class ParsingExpressionGrammar {
  import ParsingExpressionGrammar._
  
  'Grammar is 'Spacing 
}
