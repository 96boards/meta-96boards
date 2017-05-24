require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot:"

SUMMARY = "U-Boot bootloader for HiSilicon Poplar"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

SRC_URI = "git://github.com/linaro/poplar-u-boot;protocol=git;branch=latest"
SRCREV = "15a3c4c05689b7681ce9899c91861bbbdc06eb84"

PV = "v2017.05+git${SRCPV}"

# u-boot needs devtree compiler to parse dts files
DEPENDS += "dtc-native bc-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

UBOOT_EXTLINUX ??= "1"
UBOOT_EXTLINUX_LABELS ??= "default"
UBOOT_EXTLINUX_KERNEL_IMAGE_default ??= "../Image"
UBOOT_EXTLINUX_MENU_DESCRIPTION_default ??= "Linux Default"
UBOOT_EXTLINUX_FDTDIR ??= "../"
UBOOT_EXTLINUX_KERNEL_ARGS ??= "append mem=1G earlycon console=ttyAMA0"
UBOOT_EXTLINUX_ROOT ??= "root=/dev/mmcblk0p3 rootfstype=ext4 rootwait rw"
