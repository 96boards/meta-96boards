inherit systemd

SRC_URI += "file://weston.ini \
            file://weston.service \
            file://71-weston-input.rules \
            "

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini

    sed -i -e s:/etc:${sysconfdir}:g \
           -e s:/usr/bin:${bindir}:g \
              ${WORKDIR}/weston.service

    install -D -p -m0644 ${WORKDIR}/weston.service ${D}${systemd_unitdir}/system/weston.service
    install -D -p -m0644 ${WORKDIR}/71-weston-input.rules ${D}${sysconfdir}/udev/rules.d/71-weston-input.rules
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

SYSTEMD_SERVICE_${PN} = "weston.service"
