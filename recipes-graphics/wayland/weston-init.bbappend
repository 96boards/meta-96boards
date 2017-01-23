inherit systemd

SRC_URI += "file://weston.ini \
            file://weston@.service \
            file://71-weston-drm.rules \
"

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini

    # Remove upstream weston.service
    rm ${D}${systemd_unitdir}/system/weston.service

    # Install Weston systemd service and accompanying udev rule
    install -D -p -m0644 ${WORKDIR}/weston@.service ${D}${systemd_unitdir}/system/weston@.service
    sed -i -e s:/etc:${sysconfdir}:g \
           -e s:/usr/bin:${bindir}:g \
           -e s:/var:${localstatedir}:g \
              ${D}${systemd_unitdir}/system/weston@.service
    install -D -p -m0644 ${WORKDIR}/71-weston-drm.rules \
        ${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini ${systemd_unitdir}/system/weston@.service"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

SYSTEMD_SERVICE_${PN} = "weston@%i.service"
SYSTEMD_AUTO_ENABLE = "disable"
