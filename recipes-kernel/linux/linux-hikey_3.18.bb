require linux.inc

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "3.18+git${SRCPV}"
SRCREV_kernel="70ffc7c994803c3920bc90a18bdbe0058cbe6fc3"

SRC_URI = "git://github.com/96boards/linux.git;branch=hikey;name=kernel \
           file://0001-CRDA-add-full-db-into-kernel.patch \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_DEVICETREE = "hi6220-hikey.dtb"
