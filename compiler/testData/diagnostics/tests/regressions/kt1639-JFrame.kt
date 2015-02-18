package test

import javax.swing.JFrame

class KFrame() : JFrame() {
    constructor {
        val <!UNUSED_VARIABLE!>x<!> = this.rootPaneCheckingEnabled // make sure field is visible
    }
}
