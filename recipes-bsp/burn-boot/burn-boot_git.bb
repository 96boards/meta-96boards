SUMMARY = "A small python tool for downloading bootloader to ddr through serial port"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=219f23a516954274fab23350ce921da3"

SRCREV = "bee2ea1660f3a03df8d391fb75aa08dbc3441856"
SRC_URI = "git://github.com/96boards-hikey/burn-boot.git;protocol=https"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/hisi-idt.py ${D}${bindir}
}

RDEPENDS_${PN} += "python-pyserial"

inherit deploy

do_deploy() {
    install -D -p -m 0755 ${S}/hisi-idt.py ${DEPLOYDIR}/bootloader/hisi-idt.py
}

addtask deploy before do_build after do_compile

BBCLASSEXTEND = "native"
