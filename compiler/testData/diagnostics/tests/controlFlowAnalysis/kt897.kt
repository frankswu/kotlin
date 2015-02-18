//KT-897 Don't allow assignment to a property before it is defined

package kt897

class A() {
    constructor {
        <!INITIALIZATION_BEFORE_DECLARATION!>i<!> = 11
    }
    val i : Int? = null // must be an error

    constructor {
        <!INITIALIZATION_BEFORE_DECLARATION!>j<!> = 1
    }
    var j : Int = 2

    constructor {
        <!INITIALIZATION_BEFORE_DECLARATION!>k<!> = 3
    }
    val k : Int
}
