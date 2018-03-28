SUMMARY = "Modern, feature-rich, cross-platform firmware development environment for the UEFI and PI specifications"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://BaseTools/License.txt;md5=a041d47c90fd51b4514d09a5127210e6 \
                   "

DEPENDS += "util-linux-native iasl-native"

inherit deploy

PV = "0.0+${SRCPV}"

SRCREV_FORMAT = "edk2-atf"

SRCREV_edk2 = "90b10821cd3518d07e4ae106d4a02e77179e5b6f"
SRCREV_atf = "ed8112606c54d85781fc8429160883d6310ece32"
SRCREV_openplatformpkg = "b4375302ff2c26786a9d42126359c3a394a65ed4"
SRCREV_uefitools = "632d0c3c12125bbb803664e7b8c76f4adb9e6471"

SRC_URI = "git://github.com/tianocore/edk2.git;name=edk2 \
           git://github.com/ARM-software/arm-trusted-firmware.git;name=atf;destsuffix=git/atf \
           git://git.linaro.org/uefi/OpenPlatformPkg.git;name=openplatformpkg;destsuffix=git/OpenPlatformPkg \
           git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
          "

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE_juno = "(.*)"
COMPATIBLE_MACHINE = "(-)"

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

OPTEE_OS_ARG ?= ""

do_compile() {
    # Add in path to native sysroot to find uuid/uuid.h
    sed -i -e 's:-I \.\.:-I \.\. -I ${STAGING_INCDIR_NATIVE} :' ${S}/BaseTools/Source/C/Makefiles/header.makefile
    # ... and the library itself
    sed -i -e 's: -luuid: -luuid -L ${STAGING_LIBDIR_NATIVE}:g' ${S}/BaseTools/Source/C/*/GNUmakefile

    ${EDK2_DIR}/uefi-tools/uefi-build.sh -T ${AARCH64_TOOLCHAIN} -b RELEASE -a ${EDK2_DIR}/atf ${OPTEE_OS_ARG} ${UEFIMACHINE}
}

do_deploy() {
    # Placeholder to be implemented in machine specific recipe

    if [ -e ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/fip.bin ] ; then 
        install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/fip.bin ${DEPLOYDIR}/fip.bin
    fi
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
FILES_${PN} += "/boot"

addtask deploy before do_build after do_compile
