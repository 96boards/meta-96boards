FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI_append_poplar = "file://0002-compositor-fbdev.c-Temp-HACK-for-Poplar-and-Weston-w.patch \
"

SRC_URI_append_hikey = "file://0001-backend-drm-meson.build-disable-GBM-modifiers.patch \
"

SRC_URI_append_hikey-32 = "file://0001-backend-drm-meson.build-disable-GBM-modifiers.patch \
"
