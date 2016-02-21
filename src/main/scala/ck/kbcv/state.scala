package ck.kbcv

import term.Term.{F, V}
import term.util._

class State(
               val equations: ES,
               val functions: Set[(F, Int)],
               val variables: Set[V]
               )

class MutableState(s: State) {
    var equations = s.equations
    var functions = s.functions
    var variables = s.variables
}


