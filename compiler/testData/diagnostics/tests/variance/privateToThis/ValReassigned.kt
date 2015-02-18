fun <T> getT(): T = null!!

class A<in I>(init: I) {
    private val i: I

    constructor {
        i = getT()
    }

    private var i2 = i
    private val i3: I

    private var i4 = getT<I>()

    constructor {
        i2 = getT()
        i3 = init
        i4 = i3
    }
}