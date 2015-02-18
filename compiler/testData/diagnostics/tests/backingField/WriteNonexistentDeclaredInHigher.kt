val y = 1

class A() {
    constructor {
        <!INACCESSIBLE_BACKING_FIELD!>$y<!> = 1
    }
}
