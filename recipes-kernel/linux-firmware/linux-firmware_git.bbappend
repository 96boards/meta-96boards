FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# From https://github.com/TI-OpenLink/wl18xx_fw / 56d64f1f005d1a0df554c749e23be724a22a6119
SRC_URI_append = " file://wl18xx-fw-mc.bin"

do_install_append () {
    install -m 0644 ${WORKDIR}/wl18xx-fw-mc.bin ${D}/lib/firmware/ti-connectivity/
}

