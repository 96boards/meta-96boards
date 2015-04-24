SUMMARY = "Modern, feature-rich, cross-platform firmware development environment for the UEFI and PI specifications"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://BaseTools/License.txt;md5=a041d47c90fd51b4514d09a5127210e6 \
                   "

inherit deploy

PV = "0.0+${SRCPV}"

SRCREV_FORMAT = "edk2-atf"

EDKBRANCH ?= "${MACHINE_ARCH}"
SRCREV_edk2 = "53596a72cd96f84c7ca83254246f3520a49861b1"
SRCREV_edk2_hikey = "4050023046f80876ac0b9c1553e8fa7547f24c7f"

ATFBRANCH ?= "${MACHINE_ARCH}"
SRCREV_atf = "68fc81743e8671312a98c364ba2b0d69429cf4c6"
SRCREV_atf_hikey = "334a8125a6ba20f3bef6a935bcf50dbc6d886dda"

SRCREV_uefitools = "1a4887ae459b4c6242ac94fc5342c6c7200fb66c"

SRC_URI = "git://github.com/96boards/edk2.git;name=edk2;branch=${EDKBRANCH} \
           git://github.com/96boards/arm-trusted-firmware.git;name=atf;branch=${ATFBRANCH};destsuffix=git/atf \
           git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
          "

SRC_URI_append_hikey = " \
          file://0001-hikey-enable-ddr-800mhz.patch;patchdir=atf \
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

