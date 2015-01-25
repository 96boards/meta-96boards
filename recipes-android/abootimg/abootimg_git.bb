DESCRIPTION = "A tool to read/write/update android boot images"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "util-linux"

SRCREV = "7bde63e9719ce6515b9c08a47d45053afce69d3e"
SRC_URI = "git://gitorious.org/ac100/abootimg.git;protocol=https"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 abootimg ${D}${bindir}/
}

BBCLASSEXTEND = "native nativesdk"

