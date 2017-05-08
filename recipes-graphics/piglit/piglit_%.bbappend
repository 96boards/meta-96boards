SRC_URI_append_hikey = " file://0001-workaround-undefine-PIGLIT_HAS_GBM_BO_MAP.patch \
                        file://0002-workaround-don-t-try-to-test-egl_mesa_platform_surfa.patch \
"
DEPENDS_append_hikey = " virtual/egl"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
