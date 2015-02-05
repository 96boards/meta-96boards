require linux.inc

# Aarch64 needs to do some funky linking stuff which needs libgcc.a
DEPENDS_append_aarch64 = " libgcc"

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "3.18+3.19-rc7+git${SRCPV}"
SRCREV_kernel="adbffafcac56f9f9b16e1bcb9d355e3614ed56b8"

SRC_URI = "git://github.com/96boards/linux.git;branch=hikey-mainline-rebase;name=kernel \
           file://0001-CRDA-add-full-db-into-kernel.patch \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_DEVICETREE = "${S}/arch/arm64/boot/dts/hi6220-hikey.dts"
