require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

SUMMARY = "U-Boot bootloader for Arm Development Platform Juno VExpress"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
# u-boot needs devtree compiler to parse dts files
DEPENDS += "dtc-native bc-native"
SRCREV = "a624e22f083eb0af7ed868f8bdbcd198c17588ca"
PV = "v2017.06+2017.07-rc3+git${SRCPV}"

SRC_URI = "git://git.linaro.org/landing-teams/working/arm/u-boot.git;protocol=https;branch=17.10 \
"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
