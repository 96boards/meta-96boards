require linux-hikey.inc

DESCRIPTION = "Hisilicon 4.1 Kernel"

PV = "4.1+git${SRCPV}"
SRCREV_kernel="${AUTOREV}"

SRC_URI = "git://github.com/xin3liang/linux;branch=hikey-4.1-mali-r6p0;name=kernel \
           file://defconfig \
          "

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_DEVICETREE_hikey = "hisilicon/hi6220-hikey.dtb"
