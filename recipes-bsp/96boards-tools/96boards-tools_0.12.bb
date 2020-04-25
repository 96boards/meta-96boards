SUMMARY = "Useful bits an pieces to make 96Boards more standard across the board"
HOMEPAGE = "https://github.com/96boards/96boards-tools"
SECTION = "devel"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRCREV = "147e3a97050fdea7c389deb8dfe06314cf82c9ba"
SRC_URI = "git://github.com/96boards/96boards-tools;branch=master;protocol=https \
           "

S = "${WORKDIR}/git"

inherit systemd allarch update-rc.d

do_install () {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0755 ${S}/*.rules ${D}${sysconfdir}/udev/rules.d/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/resize-disk ${D}${sysconfdir}/init.d/

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/resize-helper.service ${D}${systemd_unitdir}/system

    install -d ${D}${sbindir}
    install -m 0755 ${S}/resize-helper ${D}${sbindir}
}

INITSCRIPT_NAME = "resize-disk"
INITSCRIPT_PARAMS = "start 99 5 2 . stop 20 0 1 6 ."

SYSTEMD_SERVICE_${PN} = "resize-helper.service"
RDEPENDS_${PN} += "e2fsprogs-resize2fs gptfdisk parted util-linux udev"
