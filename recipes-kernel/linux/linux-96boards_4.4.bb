require linux.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "4.4.0+git${SRCPV}"
SRCREV = "1a35563f4affa5bfe9e6ca30802e7aebf0a072d7"

SRC_URI = "git://github.com/96boards/linux.git;protocol=https;branch=96b/releases/2016.03 \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards-64bit|hikey"
KERNEL_IMAGETYPE ?= "Image"

do_configure() {
    cp ${S}/arch/arm64/configs/distro.config ${B}/.config
    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}
