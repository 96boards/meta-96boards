DESCRIPTION = "ARM Trusted Firmware Poplar"

LICENSE="BSD"
LIC_FILES_CHKSUM = "file://license.md;md5=829bdeb34c1d9044f393d5a16c068371"

COMPATIBLE_MACHINE = "poplar"

DEPENDS = " u-boot-poplar openssl-native"

SRCREV = "dc20ebf4faf350567f537e204453497666bd6f6d"

SRC_URI = "git://github.com/linaro/poplar-arm-trusted-firmware.git;name=atf;branch=latest;"

S = "${WORKDIR}/git"

# /usr/lib/atf/bl1.bin not shipped files. [installed-vs-shipped]
INSANE_SKIP_${PN} += "installed-vs-shipped"

export LDFLAGS=""

do_compile() {

    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} all fip DEBUG=1 PLAT=${COMPATIBLE_MACHINE} SPD=none \
		       BL33=${DEPLOY_DIR_IMAGE}/u-boot.bin
}

do_install() {
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/debug/bl1.bin ${D}${libdir}/atf/bl1.bin
    install -D -p -m0644 ${S}/build/${COMPATIBLE_MACHINE}/debug/fip.bin ${D}${libdir}/atf/fip.bin
}

FILES_${PN} += "${libdir}/atf/*"

