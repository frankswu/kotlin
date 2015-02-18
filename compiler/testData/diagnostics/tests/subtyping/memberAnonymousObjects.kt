class Test {
    private var x = object {};
    constructor {
        x = <!TYPE_MISMATCH!>object<!> {}
    }
}