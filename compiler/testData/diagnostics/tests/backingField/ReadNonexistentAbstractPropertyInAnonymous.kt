abstract class ReadNonexistent() {
    abstract val aa: Int
    
    constructor {
        val <!UNUSED_VARIABLE!>x<!> = <!NO_BACKING_FIELD_ABSTRACT_PROPERTY!>$aa<!>
    }
}
