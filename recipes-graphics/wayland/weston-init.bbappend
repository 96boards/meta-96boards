inherit systemd

SRC_URI += "file://weston.ini \
            file://weston-start \
            file://weston.service \
            file://weston.path \
            "

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini

    # Install weston-start script
    install -Dm755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
    sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
    sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start

    sed -i -e s:/etc:${sysconfdir}:g \
           -e s:/usr/bin:${bindir}:g \
              ${WORKDIR}/weston.service
    install -D -p -m0644 ${WORKDIR}/weston.service ${D}${systemd_unitdir}/system/weston.service
    install -D -p -m0644 ${WORKDIR}/weston.path ${D}${systemd_unitdir}/system/weston.path
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

SYSTEMD_SERVICE_${PN} = "weston.service weston.path"
