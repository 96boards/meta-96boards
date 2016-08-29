do_install_append_hikey() {
    # We cannot rely on the fact that all applications are using pkgconfig.
    # Make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi

    # Remove Mesa libraries (EGL, GLESv1, GLESv2, GBM and Wayland-egl)
    # provided by ARM Mali Utgard GPU User Space driver for HiKey
    rm -f ${D}/${libdir}/libEGL*
    rm -f ${D}/${libdir}/libGLESv1_CM.*
    rm -f ${D}/${libdir}/libGLESv2.*
    rm -f ${D}/${libdir}/libgbm.*
    rm -f ${D}/${libdir}/libwayland-egl.*
}

do_install_append_hikey-32() {
    # We cannot rely on the fact that all applications are using pkgconfig.
    # Make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi

    # Remove Mesa libraries (EGL, GLESv1, GLESv2, GBM and Wayland-egl)
    # provided by ARM Mali Utgard GPU User Space driver for HiKey
    rm -f ${D}/${libdir}/libEGL*
    rm -f ${D}/${libdir}/libGLESv1_CM.*
    rm -f ${D}/${libdir}/libGLESv2.*
    rm -f ${D}/${libdir}/libgbm.*
    rm -f ${D}/${libdir}/libwayland-egl.*
}
PROVIDES_remove_hikey = "virtual/libgles1 virtual/libgles2 virtual/egl"
PROVIDES_remove_hikey-32 = "virtual/libgles1 virtual/libgles2 virtual/egl"
