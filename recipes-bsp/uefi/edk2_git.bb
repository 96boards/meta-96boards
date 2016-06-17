SUMMARY = "Modern, feature-rich, cross-platform firmware development environment for the UEFI and PI specifications"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://BaseTools/License.txt;md5=a041d47c90fd51b4514d09a5127210e6 \
                   "

DEPENDS += "util-linux-native iasl-native"

inherit deploy

PV = "0.0+${SRCPV}"

SRCREV_FORMAT = "edk2-atf"

SRCREV_edk2 = "53596a72cd96f84c7ca83254246f3520a49861b1"
SRCREV_atf = "68fc81743e8671312a98c364ba2b0d69429cf4c6"
SRCREV_openplatformpkg = "1269d3fa0d2e8704be9c8ad74d320ce91c8ff8cb"
SRCREV_uefitools = "d30846ab593f8e525c5b0f4399406d0ac8e69002"

SRC_URI = "git://github.com/tianocore/edk2.git;name=edk2 \
           git://github.com/ARM-software/arm-trusted-firmware.git;name=atf;destsuffix=git/atf \
           git://git.linaro.org/uefi/OpenPlatformPkg.git;name=openplatformpkg;destsuffix=git/OpenPlatformPkg \
          "

SRC_URI_append = "git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
                 "

S = "${WORKDIR}/git"

export AARCH64_TOOLCHAIN = "GCC49"
export EDK2_DIR = "${S}"

export CROSS_COMPILE_64 = "${TARGET_PREFIX}"
export CROSS_COMPILE_32 = "${TARGET_PREFIX}"

# Override variables from BaseTools/Source/C/Makefiles/header.makefile
# to build BaseTools with host toolchain
export CC = "${BUILD_CC}"
export CXX = "${BUILD_CXX}"
export AS = "${BUILD_CC}"
export AR = "${BUILD_AR}"
export LD = "${BUILD_LD}"
export LINKER = "${CC}"

# This is a bootloader, so unset OE LDFLAGS.
# OE assumes ld==gcc and passes -Wl,foo
LDFLAGS = ""

export UEFIMACHINE ?= "${MACHINE_ARCH}"

do_compile() {
    # Add in path to native sysroot to find uuid/uuid.h
    sed -i -e 's:-I \.\.:-I \.\. -I ${STAGING_INCDIR_NATIVE} :' ${S}/BaseTools/Source/C/Makefiles/header.makefile
    # ... and the library itself
    sed -i -e 's: -luuid: -luuid -L ${STAGING_LIBDIR_NATIVE}:g' ${S}/BaseTools/Source/C/*/GNUmakefile

    ${EDK2_DIR}/uefi-tools/uefi-build.sh -b RELEASE -a ${EDK2_DIR}/atf ${UEFIMACHINE}
}

do_deploy() {
    # Placeholder to be implemented in machine specific recipe
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
FILES_${PN} += "/boot"

addtask deploy before do_build after do_compile
