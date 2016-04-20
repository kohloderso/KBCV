
package ck.kbcv

import term.Term.{F, V}
import term.lpo.Precedence
import term.reco.{IES, ERCH}
import term.util._

class State(
           val e0: IES,
           val erc: ERCH,
           val precedence: Precedence,
           val functions: Set[(F, Int)],
           val variables: Set[V],
           val depth: Int)

class MutableState(s: State) {
    var e0 = s.e0
    var erc = s.erc
    var precedence = s.precedence
    var functions = s.functions
    var variables = s.variables
    var depth = s.depth
}
