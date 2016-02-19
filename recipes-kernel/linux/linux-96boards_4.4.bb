require linux.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "4.3.0+git${SRCPV}"
SRCREV_kernel="01207011a6ac898f6b2c179dc3372de28eea9f69"

SRC_URI = "git://github.com/rsalveti/linux;name=kernel;branch=reference-qcom-rebase \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards-64bit"
KERNEL_IMAGETYPE ?= "Image"

do_configure() {
    oe_runmake -C ${S} defconfig
}
