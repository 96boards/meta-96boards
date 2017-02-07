SRC_URI += "file://xserver-nodm@.service \
            file://71-xserver-drm.rules \
"

do_install_append() {
    # Remove upstream xserver-nodm.service
    rm ${D}${systemd_unitdir}/system/xserver-nodm.service

    # Install Xserver systemd service and accompanying udev rule
    install -D -p -m0644 ${WORKDIR}/xserver-nodm@.service \
        ${D}${systemd_unitdir}/system/xserver-nodm@.service
    sed -i -e s:/etc:${sysconfdir}:g \
           -e s:/usr/bin:${bindir}:g \
           -e s:/var:${localstatedir}:g \
              ${D}${systemd_unitdir}/system/xserver-nodm@.service
    install -D -p -m0644 ${WORKDIR}/71-xserver-drm.rules \
        ${D}${sysconfdir}/udev/rules.d/71-xserver-drm.rules
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${systemd_unitdir}/system/xserver-nodm@.service"

SYSTEMD_SERVICE_${PN} = "xserver-nodm@%i.service"
SYSTEMD_AUTO_ENABLE = "disable"
