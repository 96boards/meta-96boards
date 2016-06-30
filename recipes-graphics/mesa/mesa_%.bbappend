# Remove the *.so libraries - the ones provided by mali-450 driver are to
# be used for HiKey
do_install_append_hikey() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi

    # Workarounds for Hikey board

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
