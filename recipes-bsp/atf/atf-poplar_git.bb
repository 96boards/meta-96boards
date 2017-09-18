require atf.inc

DESCRIPTION = "ARM Trusted Firmware Poplar"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://license.md;md5=829bdeb34c1d9044f393d5a16c068371"

COMPATIBLE_MACHINE = "poplar"

DEPENDS += "u-boot-poplar"

SRCREV = "dc20ebf4faf350567f537e204453497666bd6f6d"
SRC_URI = "git://github.com/linaro/poplar-arm-trusted-firmware.git;name=atf;branch=latest;"
S = "${WORKDIR}/git"
