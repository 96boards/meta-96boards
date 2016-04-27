require linux.inc
require linux-optee.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "4.4.0+git"
SRCREV_kernel = "1a35563f4affa5bfe9e6ca30802e7aebf0a072d7"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://github.com/96boards/linux.git;protocol=https;branch=96b/releases/2016.03;name=kernel \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards-64|hikey"
KERNEL_IMAGETYPE ?= "Image"
# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOSTCFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    cp ${S}/arch/arm64/configs/distro.config ${B}/.config
    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}
