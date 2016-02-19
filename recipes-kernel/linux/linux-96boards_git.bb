require linux.inc

SUMMARY = "96boards Kernel"

PV = "4.4+git${SRCPV}"
SRCREV_kernel="5cb53a728ed9d051dafbb29667e5742e12346fa8"

SRC_URI = "git://git.linaro.org/git-ro/people/amit.kucheria/kernel.git;branch=96b/releases/2016.03;name=kernel;protocol=http \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey"
KERNEL_IMAGETYPE ?= "Image"

