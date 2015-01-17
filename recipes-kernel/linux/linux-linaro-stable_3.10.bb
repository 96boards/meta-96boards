require linux.inc

# Aarch64 needs to do some funky linking stuff which needs libgcc.a
DEPENDS_append_aarch64 = " libgcc"

DESCRIPTION = "Linaro Stable 3.10 Kernel"

PV = "3.10.64+git${SRCPV}"
SRCREV_kernel="2156eda2aafa0b0f660656246a11d7c2a9947ff9"

SRC_URI = "git://git.linaro.org/git/kernel/linux-linaro-stable.git;protocol=http;branch=linux-linaro-lsk;name=kernel \
           file://v8r2-kernel.tar;unpack=false \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "96boards.*"
KERNEL_IMAGETYPE ?= "Image"

do_compile_prepend() {
    tar xf ${WORKDIR}/v8r2-kernel.tar -C ${S}/arch/arm64/
}
