require linux.inc

# Aarch64 needs to do some funky linking stuff which needs libgcc.a
DEPENDS_append_aarch64 = " libgcc"

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "3.18+git${SRCPV}"
SRCREV_kernel="c94247aa394cfe5a92e124e8a3f088e2cda9ad29"

SRC_URI = "git://koen.kooi@hisilicon.git.linaro.org/srv/landing-teams.git.linaro.org/hisilicon/v8r2-kernel.git;protocol=ssh;branch=hikey-android;name=kernel \
           file://0001-CRDA-add-full-db-into-kernel.patch \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_DEVICETREE = "${S}/arch/arm64/boot/dts/hi6220-hikey.dts"
