require linux.inc

# Aarch64 needs to do some funky linking stuff which needs libgcc.a
DEPENDS_append_aarch64 = " libgcc"

DESCRIPTION = "Linaro Stable 3.10 Kernel"

PV = "3.10.52+git${SRCPV}"
SRCREV_kernel="abcf930f8867327eba17e5f5081ac5fad7a411e9"

SRC_URI = "git://koen.kooi@hisilicon.git.linaro.org/srv/landing-teams.git.linaro.org/hisilicon/v8r2-kernel.git;protocol=ssh;branch=lcb_bringup;name=kernel \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

