require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

SUMMARY = "U-Boot bootloader for OrangePi i96"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"
# u-boot needs devtree compiler to parse dts files
DEPENDS += "dtc-native bc-native"
SRCREV = "976c9ecfc85a17ea4681c1eb2d1cf8f12251311b"
PV = "v2012.04.01+git${SRCPV}"

SRC_URI = "git://github.com/daniel-thompson/u-boot.git;nobranch=1"

S = "${WORKDIR}/git"

# The toolset libraries use the hard float ABI but u-boot is built with
# the soft float ABI  and liberally uses intrinsics. We need to use a
# private libgcc in order to get link compatible intrinsics.
EXTRA_OEMAKE += 'USE_PRIVATE_LIBGCC=yes'

PACKAGE_ARCH = "${MACHINE_ARCH}"

# OrangePi u-boot doesn't actually support extlinux yet (but it will
# when/if it is updated to the latest version)
UBOOT_EXTLINUX ??= "1"
UBOOT_EXTLINUX_LABELS ??= "default"
UBOOT_EXTLINUX_KERNEL_IMAGE_default ??= "../zImage"
UBOOT_EXTLINUX_MENU_DESCRIPTION_default ??= "Linux Default"
UBOOT_EXTLINUX_FDTDIR ??= "../"
UBOOT_EXTLINUX_KERNEL_ARGS ??= "append console=ttyS0,921600"
UBOOT_EXTLINUX_ROOT ??= "root=/dev/mmcblk0p2 rootfstype=ext4 rootwait rw"
