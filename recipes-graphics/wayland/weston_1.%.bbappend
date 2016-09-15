FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_hikey = "file://0001-force-software-cursor.patch \
                        file://0001-libinput-seat-compositor-Don-t-fail-on-missing-devic.patch \
                        "
