FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_hikey = " file://0001-force-software-cursor.patch \
"

SRC_URI_append_poplar = "file://0001-compositor-fbdev.c-Temp-HACK-for-Poplar-and-Weston-w.patch \
"
