FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_poplar = "\
    file://0002-compositor-fbdev.c-Temp-HACK-for-Poplar-and-Weston-w.patch \
"
