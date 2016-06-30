do_install_append_hikey() {
    #because we cannot rely on the fact that all apps will use pkgconfig,
    #make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi

    # Workarounds for Hikey board: remove the mesa libraries which duplicate
    # the ones provided by mali450 driver

    # remove EGL
    rm -f ${D}/${libdir}/libEGL*
    # remove GLESv1
    rm -f ${D}/${libdir}/libGLESv1_CM.*
    # remove GLESv2
    rm -f ${D}/${libdir}/libGLESv2.*
    # remove GBM
    rm -f ${D}/${libdir}/libgbm.*
    # remove Wayland-egl
    rm -f ${D}/${libdir}/libwayland-egl.*
}

PROVIDES_remove_hikey = "virtual/libgles1 virtual/libgles2 virtual/egl"
