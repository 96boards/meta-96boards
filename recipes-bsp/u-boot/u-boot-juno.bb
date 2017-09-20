SUMMARY = "U-Boot bootloader for Arm Development Platform Juno VExpress"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
# u-boot needs devtree compiler to parse dts files
DEPENDS += "dtc-native bc-native"
SRCREV = "24c6ee8c9d6f0768ced0a564191b0d6676922550"
PV = "v2017.03+git${SRCPV}"

require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

SRC_URI = "git://git.linaro.org/landing-teams/working/arm/u-boot.git;protocol=https;branch=17.04 \
    file://0001-tools-disable-_libfdt.so-swig-present-python-dev-mis.patch \
"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
