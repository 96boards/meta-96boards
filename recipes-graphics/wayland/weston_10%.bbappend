FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:poplar = "\
    file://0002-compositor-fbdev.c-Temp-HACK-for-Poplar-and-Weston-w.patch \
"
