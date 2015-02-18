class A {
    val x: Int
    val y: Int
    <!EXPECTED_CONSTRUCTOR_KEYWORD_BEFORE_CLASS_INITIALIZER!>{
    x = 1
    }<!>
    constructor {
        y = 1
    }
}
