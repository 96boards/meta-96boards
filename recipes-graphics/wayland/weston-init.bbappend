SRC_URI += "file://weston.ini"

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"
