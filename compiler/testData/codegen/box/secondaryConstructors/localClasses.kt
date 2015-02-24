
open class C(val grandFatherProp: String)

fun box(): String {
    var sideEffects: String = ""
    var parentSideEffects: String = ""
    val justForUsageInClosure = 7
    val justForUsageInParentClosure = "#2"

    abstract class B : C {
        val parentProp: String
        constructor {
            sideEffects += "minus-one#"
            parentSideEffects += "1"
        }
        protected constructor(arg: Int): super(justForUsageInParentClosure) {
            parentProp = (arg).toString()
            sideEffects += "0.5#"
            parentSideEffects += justForUsageInParentClosure
        }
        protected constructor(arg1: Int, arg2: Int): super(justForUsageInParentClosure) {
            parentProp = (arg1 + arg2).toString()
            sideEffects += "0.7#"
            parentSideEffects += "#3"
        }
        constructor {
            sideEffects += "zero#"
            parentSideEffects += "#4"
        }
    }

    class A : B {
        var prop: String = ""
        constructor {
            sideEffects += prop + "first"
        }

        constructor(x1: Int, x2: Int): super(x1, x2) {
            prop = x1.toString()
            sideEffects += "#third"
        }

        constructor {
            sideEffects += prop + "#second"
        }

        constructor(x: Int): super(justForUsageInClosure - 4 + x) {
            prop += "${x}#int"
            sideEffects += "#fourth"
        }

        constructor(): this(justForUsageInClosure) {
            sideEffects += "#fifth"
        }
    }

    val a1 = A(5, 10)
    if (a1.prop != "5") return "fail0: ${a1.prop}"
    if (a1.parentProp != "15") return "fail1: ${a1.parentProp}"
    if (sideEffects != "minus-one#zero#0.7#first#second#third") return "fail2: ${sideEffects}"
    if (parentSideEffects != "1#4#3") return "fail2.5: ${parentSideEffects}"

    sideEffects = ""
    parentSideEffects = ""
    val a2 = A(123)
    if (a2.prop != "123#int") return "fail3: ${a2.prop}"
    if (a2.parentProp != "126") return "fail4: ${a2.parentProp}"
    if (sideEffects != "minus-one#zero#0.5#first#second#fourth") return "fail5: ${sideEffects}"
    if (parentSideEffects != "1#4#2") return "fail5.5: ${parentSideEffects}"

    sideEffects = ""
    parentSideEffects = ""
    val a3 = A()
    if (a3.prop != "7#int") return "fail6: ${a3.prop}"
    if (a3.parentProp != "10") return "fail7: ${a3.parentProp}"
    if (sideEffects != "minus-one#zero#0.5#first#second#fourth#fifth") return "fail8: ${sideEffects}"
    if (parentSideEffects != "1#4#2") return "fail8.5: ${parentSideEffects}"

    return "OK"
}
