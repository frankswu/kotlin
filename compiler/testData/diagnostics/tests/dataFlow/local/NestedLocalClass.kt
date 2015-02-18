fun test(x: Any) {
  if (x !is String) return

  class LocalOuter {
    inner class Local {
      constructor {
        <!DEBUG_INFO_SMARTCAST!>x<!>.length()
      }
    }
  }
}
