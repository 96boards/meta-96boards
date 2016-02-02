require linux-hikey.inc

DESCRIPTION = "Hisilicon 4.5 Kernel"

PV = "4.5+git${SRCPV}"

SRCREV_kernel="623d1d7bbd697e5793a135d5334e02c0e9889ba1"

SRC_URI = "git://github.com/xin3liang/linux;branch=hikey-tracking-integration-devel-drm;name=kernel \
           file://defconfig  \
          "

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_DEVICETREE_hikey = "hisilicon/hi6220-hikey.dtb"

