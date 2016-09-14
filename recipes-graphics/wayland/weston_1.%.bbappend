FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_hikey = "file://0001-force-software-cursor.patch \
                        file://0001-Add-configuration-option-for-no-input-device.patch \
                        "
