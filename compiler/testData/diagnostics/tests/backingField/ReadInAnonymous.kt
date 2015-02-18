class ReadByAnotherPropertyInitializer() {
    val a = 1
    constructor {
        val <!UNUSED_VARIABLE!>x<!> = $a
    }
}
