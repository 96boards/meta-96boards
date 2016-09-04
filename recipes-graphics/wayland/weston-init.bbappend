inherit systemd

SRC_URI += "file://weston.ini \
            file://weston-start \
            file://weston.service \
            file://71-weston-drm.rules \
"

do_install_append() {
    install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini

    # Install weston-start script
    install -D -p -m0755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
    sed -i -e s:@DATADIR@:${datadir}:g \
           -e s:@LOCALSTATEDIR@:${localstatedir}:g \
              ${D}${bindir}/weston-start

    # Install Weston systemd service and accompanying udev rule
    install -D -p -m0644 ${WORKDIR}/weston.service ${D}${systemd_unitdir}/system/weston.service
    sed -i -e s:/etc:${sysconfdir}:g \
           -e s:/usr/bin:${bindir}:g \
              ${D}${systemd_unitdir}/system/weston.service
    install -D -p -m0644 ${WORKDIR}/71-weston-drm.rules \
        ${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules
}

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini"

SYSTEMD_SERVICE_${PN} = "weston.service"
