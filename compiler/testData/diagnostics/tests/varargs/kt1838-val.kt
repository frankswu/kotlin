class A(vararg val t : Int) {
    constructor {
        val <!UNUSED_VARIABLE!>t1<!> : IntArray = t
    }
}
