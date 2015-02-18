class A {
    ann constructor {}
    [ann] constructor {}
    <!UNRESOLVED_REFERENCE!>aaa<!> constructor {}
    [<!UNRESOLVED_REFERENCE!>aaa<!>] constructor {}
}

trait T {
    <!ANONYMOUS_INITIALIZER_IN_TRAIT!>ann constructor {}<!>
    <!ANONYMOUS_INITIALIZER_IN_TRAIT!>[ann] constructor {}<!>
    <!ANONYMOUS_INITIALIZER_IN_TRAIT!><!UNRESOLVED_REFERENCE!>aaa<!> constructor {}<!>
    <!ANONYMOUS_INITIALIZER_IN_TRAIT!>[<!UNRESOLVED_REFERENCE!>aaa<!>] constructor {}<!>
}

annotation class ann