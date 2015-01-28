require linux.inc

# Aarch64 needs to do some funky linking stuff which needs libgcc.a
DEPENDS_append_aarch64 = " libgcc"

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "3.18+git${SRCPV}"
SRCREV_kernel="573860313be83371aecfee0b75e02d2f45dedd8a"

SRC_URI = "git://koen.kooi@hisilicon.git.linaro.org/srv/landing-teams.git.linaro.org/hisilicon/v8r2-kernel.git;protocol=ssh;branch=hikey-test-framebuffer-console;name=kernel \
           file://0001-CRDA-add-full-db-into-kernel.patch \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

