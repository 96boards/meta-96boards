SRC_URI += "file://weston.ini \
            file://weston-start \
            "

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini

    # Install weston-start script
    install -Dm755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
    sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
    sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"
