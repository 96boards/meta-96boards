require linux.inc
require linux-optee.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "4.4.8+git"
SRCREV_kernel = "83c121882f0db55e5195f458d5c0217d6b08edf2"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://github.com/96boards/linux.git;protocol=https;branch=96b/releases/2016.06;name=kernel \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards-64|96boards-32|vexpress-juno|hikey"
KERNEL_IMAGETYPE ?= "Image"
# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOSTCFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    cp ${S}/arch/arm64/configs/distro.config ${B}/.config
    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}
