require linux.inc

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "3.18+git${SRCPV}"
SRCREV_kernel="7bfade6d37fc2eb28b9c9f26219b84fd1e2c8f28"

SRC_URI = "git://github.com/96boards/linux.git;branch=hikey;name=kernel \
           file://0001-CRDA-add-full-db-into-kernel.patch \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_DEVICETREE = "hi6220-hikey.dtb"
