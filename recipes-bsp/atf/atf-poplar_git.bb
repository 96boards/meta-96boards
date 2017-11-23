DESCRIPTION = "ARM Trusted Firmware Poplar"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://license.rst;md5=33065335ea03d977d0569f270b39603e"
DEPENDS += "u-boot-poplar"
SRCREV = "812fae9e5ee80ddad6bae6bf1c403c9ffaaae984"

SRC_URI = "git://github.com/linaro/poplar-arm-trusted-firmware.git;name=atf;branch=latest"

S = "${WORKDIR}/git"

require atf.inc

COMPATIBLE_MACHINE = "poplar"

# ATF requires u-boot.bin file. Ensure it's deployed before we compile.
do_compile[depends] += "u-boot-poplar:do_deploy"

do_compile() {
    oe_runmake \
      CROSS_COMPILE=${TARGET_PREFIX} \
      all \
      fip \
      PLAT=${COMPATIBLE_MACHINE} \
      SPD=none \
      BL33=${DEPLOY_DIR_IMAGE}/u-boot.bin
}

do_install_append() {
           install -Dm644 ${WORKDIR}/git/plat/hisilicon/poplar/include/poplar_layout.h ${D}${includedir}/poplar_layout.h
}
