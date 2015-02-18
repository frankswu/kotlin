//KT-510 `this.` allows initialization without backing field

package kt510

public open class Identifier1() {
    var field : Boolean
    constructor {
        field = false; // error
    }
}


public open class Identifier2() {
    var field : Boolean
    constructor {
        this.field = false;
    }
}
