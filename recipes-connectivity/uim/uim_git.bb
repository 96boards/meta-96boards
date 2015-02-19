DESCRIPTION = "User Mode Initialization Manager for TI wilink devices"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://uim.c;startline=4;endline=12;md5=16a9d6e829218481f8a61797fe2be2e2"

inherit systemd

# Doesn't have a version, so we'll make one up
PV = "0.0"

SRCREV = "b36b39cbfb0d2c6a3b9231d530b2bd4ca234421e"
SRC_URI = "git://github.com/96boards/uim.git"

S= "${WORKDIR}/git"

do_install() {
	oe_runmake install DESTDIR=${D}
	
	install -d ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/systemd/system/ti-uim.service ${D}${systemd_unitdir}/system

	install -d ${D}/lib/udev/rules.d
	install -m 0644 ${S}/udev/rules.d/60-ti-uim.rules ${D}/lib/udev/rules.d
}

SYSTEMD_SERVICE_${PN} = "ti-uim.service"

# connman has a plugin to handle all this
RCONFLICTS_${PN} += "connman-plugin-tist"
RRECOMMENDS_${PN} = "linux-firmware-wl12xx"
