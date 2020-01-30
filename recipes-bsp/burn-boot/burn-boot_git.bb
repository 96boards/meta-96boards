SUMMARY = "A small python tool for downloading bootloader to ddr through serial port"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=219f23a516954274fab23350ce921da3"

SRCREV = "6d8429dd5dfa4ec1cee4428cafe882c16624832a"
SRC_URI = "git://github.com/96boards-hikey/burn-boot.git;protocol=https \
"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/hisi-idt.py ${D}${bindir}
}

RDEPENDS_${PN} += "python3-core python3-pyserial"

inherit deploy

do_deploy() {
    install -D -p -m 0755 ${S}/hisi-idt.py ${DEPLOYDIR}/bootloader/hisi-idt.py
}

addtask deploy before do_build after do_compile

BBCLASSEXTEND = "native"
