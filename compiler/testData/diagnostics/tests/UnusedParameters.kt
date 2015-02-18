class C(a: Int, b: Int, c: Int, d: Int, <!UNUSED_PARAMETER!>e<!>: Int = d, val f: String) {
    constructor {
        a + a
    }

    val g = b

    constructor {
        c + c
    }
}

fun f(a: Int, b: Int, <!UNUSED_PARAMETER!>c<!>: Int = b) {
    a + a
}