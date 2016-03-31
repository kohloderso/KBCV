
package ck.kbcv

import term.Term.{F, V}
import term.lpo.Precedence
import term.reco.ERCH
import term.util._

class State(
           val erc: ERCH,
           val precedence: Precedence,
               val equations: ES,
               val functions: Set[(F, Int)],
               val variables: Set[V]
               )

class MutableState(s: State) {
    var erc = s.erc
    var precedence = s.precedence
    var equations = s.equations
    var functions = s.functions
    var variables = s.variables
}
