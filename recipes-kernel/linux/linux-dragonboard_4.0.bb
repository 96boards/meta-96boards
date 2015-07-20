require linux.inc

DESCRIPTION = "Hisilicon 3.18 Kernel"

PV = "4.0.0+git${SRCPV}"
SRCREV_kernel="d035b17312540f6788f8757095aefb2046ac5cff"

SRC_URI = "git://git.linaro.org/landing-teams/working/qualcomm/kernel.git;name=kernel;protocol=http;branch=release/qcomlt-4.0 \
           file://defconfig \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "dragonboard-410c"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_DEVICETREE = "qcom/apq8016-sbc.dtb"

# Wifi firmware has a recognizable arch :( 
ERROR_QA_remove = "arch"
