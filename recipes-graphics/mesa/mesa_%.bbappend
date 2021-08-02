PROVIDES:remove = "${@bb.utils.contains("MACHINE_FEATURES", "mali450", "virtual/libgles1 virtual/libgles2 virtual/egl virtual/libgbm", "", d)}"

do_install:append:hikey() {
        rm -rf ${D}${libdir}/libEGL*
        rm -rf ${D}${libdir}/libGLESv2.*
        rm -rf ${D}${libdir}/libgbm*
        rm -rf ${D}${libdir}/libGLESv1_CM.*
        rm -rf ${D}${libdir}/libwayland-*
}
