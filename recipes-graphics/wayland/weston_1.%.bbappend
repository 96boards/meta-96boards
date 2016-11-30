FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_hikey = "file://0001-force-software-cursor.patch \
                        file://weston-1.11-config-option-for-no-input-device.patch \
"
