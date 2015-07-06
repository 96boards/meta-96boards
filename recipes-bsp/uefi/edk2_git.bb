SUMMARY = "Modern, feature-rich, cross-platform firmware development environment for the UEFI and PI specifications"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://BaseTools/License.txt;md5=a041d47c90fd51b4514d09a5127210e6 \
                   "

DEPENDS += "util-linux-native"

inherit deploy

PV = "0.0+${SRCPV}"

SRCREV_FORMAT = "edk2-atf"

EDKBRANCH ?= "${MACHINE_ARCH}"
SRCREV_edk2 = "53596a72cd96f84c7ca83254246f3520a49861b1"
SRCREV_edk2_hikey = "9ad60ff668a3b9c85878705108f054125757b4cb"

ATFBRANCH ?= "${MACHINE_ARCH}"
SRCREV_atf = "68fc81743e8671312a98c364ba2b0d69429cf4c6"
SRCREV_atf_hikey = "46d70fb5a302b12f543c7b8b637d96c6ecffee48"

SRCREV_uefitools = "869b77de3357868643ac558fec490a9fd507cd63"

SRC_URI = "git://github.com/96boards/edk2.git;name=edk2;branch=${EDKBRANCH} \
           git://github.com/96boards/arm-trusted-firmware.git;name=atf;branch=${ATFBRANCH};destsuffix=git/atf \
           git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
	   file://0001-accomodate-OE-to-let-it-provide-its-own-native-sysro.patch \
	   file://0001-Check-the-result-of-fread.patch \
          "

S = "${WORKDIR}/git"

export AARCH64_TOOLCHAIN = "GCC49"
export EDK2_DIR = "${S}"
export UEFI_TOOLS_DIR = "${S}/uefi-tools"
export CROSS_COMPILE_64 = "${TARGET_PREFIX}"
export CROSS_COMPILE_32 = "${TARGET_PREFIX}"

# Workaround a gcc 4.9 feature
# https://lists.96boards.org/pipermail/dev/2015-March/000146.html 
CFLAGS = " -fno-delete-null-pointer-checks"
BUILD_CFLAGS += "-Wno-error=unused-result"

# This is a bootloader, so unset OE LDFLAGS.
# OE assumes ld==gcc and passes -Wl,foo
LDFLAGS = ""

export UEFIMACHINE ?= "${MACHINE_ARCH}"

do_compile() {
    ${UEFI_TOOLS_DIR}/uefi-build.sh -b RELEASE -a ${S}/atf ${UEFIMACHINE}
}

do_install() {
    install -d ${D}/boot
    install -m 0644 ${S}/atf/build/${UEFIMACHINE}/release/*.bin ${D}/boot/
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/atf/build/${UEFIMACHINE}/release/*.bin ${DEPLOYDIR}/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
FILES_${PN} += "/boot"

addtask deploy before do_build after do_compile

