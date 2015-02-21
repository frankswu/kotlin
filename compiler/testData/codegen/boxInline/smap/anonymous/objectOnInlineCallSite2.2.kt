package builders

inline fun call(inlineOptions(InlineOption.ONLY_LOCAL_RETURN) init: () -> Unit) {
    return init()
}

inline fun test(): String {
    var res = "Fail"

    call {
        object {
            fun run () {
                res = "OK"
            }
        }.run()
    }

    return res
}


//SMAP
//objectOnInlineCallSite2.2.kt
//Kotlin
//*S Kotlin
//*F
//+ 1 objectOnInlineCallSite2.2.kt
//builders/BuildersPackage$objectOnInlineCallSite2_2$HASH$test$1$1
//*L
//1#1,22:1
//*E
//
//SMAP
//objectOnInlineCallSite2.2.kt
//Kotlin
//*S Kotlin
//*F
//+ 1 objectOnInlineCallSite2.2.kt
//builders/BuildersPackage
//*L
//1#1,22:1
//*E
//
//SMAP
//objectOnInlineCallSite2.2.kt
//Kotlin
//*S Kotlin
//*F
//+ 1 objectOnInlineCallSite2.2.kt
//builders/BuildersPackage$objectOnInlineCallSite2_2$HASH$test$1$1
//*L
//1#1,22:1
//*E
