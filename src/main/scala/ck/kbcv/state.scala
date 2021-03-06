
package ck.kbcv

import term.Term.{F, V}
import term.lpo.Precedence
import term.reco.{ERCH, IES, OLS}

class State(
           val e0: IES,
           val erc: ERCH,
           val precedence: Precedence,
           val functions: Set[(F, Int)],
           val variables: Set[V],
           val depth: Int,
           val ols: OLS,
           val message: String)

class MutableState(s: State) {
    var e0 = s.e0
    var erc = s.erc
    var precedence = s.precedence
    var functions = s.functions
    var variables = s.variables
    var depth = s.depth
    var ols = s.ols
    var message = s.message
}

object UndoRedoType extends Enumeration {
    type UndoRedoType = UndoRedoType.Value
    val UNDO, REDO, CURR = Value
}
