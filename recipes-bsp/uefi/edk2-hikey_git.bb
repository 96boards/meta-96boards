require edk2_git.bb

COMPATIBLE_MACHINE = "hikey"

DEPENDS_append = " dosfstools-native gptfdisk-native mtools-native virtual/fakeroot-native grub optee-os"

inherit deploy pythonnative

SRCREV_edk2 = "8c9e0d9bc5582ca1f4d7bc14c11c1ae4831c0118"
SRCREV_atf = "10787b0519afce1e887a935789b2d624849856a9"
SRCREV_openplatformpkg = "8469c8c168d07b71bc594c8790bf3d93bfa28f2b"
SRCREV_uefitools = "42eac07beb4da42a182d2a87d6b2e928fc9a31cf"
SRCREV_lloader = "79530f6d9b2668e2d3a76adadcc0053015937e82"
SRCREV_atffastboot = "5b0d44c057bc0005965da990e8add72670810996"

### DISCLAIMER ###
# l-loader should be built with an aarch32 toolchain but we target an
# ARMv8 machine. OE cross-toolchain is aarch64 in this case.
# We decided to use an external pre-built toolchain in order to build
# l-loader.
# knowledgeably, it is a hack...
###
SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=testing/hikey960_v2.5 \
           git://github.com/ARM-software/arm-trusted-firmware.git;name=atf;branch=integration;destsuffix=git/atf \
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=testing/hikey960_v1.3.4;destsuffix=git/OpenPlatformPkg \
           git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
           git://github.com/96boards-hikey/l-loader.git;name=lloader;branch=testing/hikey960_v1.2;destsuffix=git/l-loader \
           git://github.com/96boards-hikey/atf-fastboot.git;name=atffastboot;destsuffix=git/atf-fastboot \
           http://releases.linaro.org/components/toolchain/binaries/6.4-2017.08/arm-linux-gnueabihf/gcc-linaro-6.4.1-2017.08-x86_64_arm-linux-gnueabihf.tar.xz;name=tc \
           file://grub.cfg.in \
          "

SRC_URI[tc.md5sum] = "8c6084924df023d1e5c0bac2a4ccfa2f"
SRC_URI[tc.sha256sum] = "1c975a1936cc966099b3fcaff8f387d748caff27f43593214ae6d4601241ae40"

# /usr/lib/edk2/bl1.bin not shipped files. [installed-vs-shipped]
INSANE_SKIP_${PN} += "installed-vs-shipped"

OPTEE_OS_ARG = "-s ${EDK2_DIR}/optee_os"

# workaround EDK2 is confused by the long path used during the build
# and truncate files name expected by VfrCompile
do_patch[postfuncs] += "set_max_path"
set_max_path () {
    sed -i -e 's/^#define MAX_PATH.*/#define MAX_PATH 511/' ${S}/BaseTools/Source/C/VfrCompile/EfiVfr.h
}

do_compile_prepend() {
    # Fix hardcoded value introduced in
    # https://git.linaro.org/uefi/uefi-tools.git/commit/common-functions?id=65e8e8df04f34fc2a87ae9d34f5ef5b6fee5a396
    sed -i -e 's/aarch64-linux-gnu-/${TARGET_PREFIX}/' ${S}/uefi-tools/common-functions

    # When using upstream ATF, we should set TOS_BIN to tee-pager.bin
    sed -i -e 's/^TOS_BIN=tee.bin/TOS_BIN=tee-pager.bin/' ${S}/uefi-tools/platforms.config

    # We need the secure payload (Trusted OS) built from OP-TEE Trusted OS (tee-pager.bin)
    # but we have already built tee-pager.bin from optee-os recipe
    # Copy tee-pager.bin and create dummy files to make uefi-build.sh script happy
    install -D -p -m0644 \
      ${STAGING_DIR_HOST}${nonarch_base_libdir}/firmware/tee-pager.bin \
      ${EDK2_DIR}/optee_os/out/arm-plat-hikey/core/tee-pager.bin

    # opteed-build.sh script has a few assumptions...
    mkdir -p ${EDK2_DIR}/optee_os/documentation
    touch ${EDK2_DIR}/optee_os/documentation/optee_design.md

    printf "all:\n"  > ${EDK2_DIR}/optee_os/Makefile
    printf "\ttrue" >> ${EDK2_DIR}/optee_os/Makefile
}

do_compile_append() {
    # Use pre-built aarch32 toolchain
    export PATH=${WORKDIR}/gcc-linaro-6.4.1-2017.08-x86_64_arm-linux-gnueabihf/bin:$PATH

    # HiKey requires an ATF fork for the recovery mode
    cd ${EDK2_DIR}/atf-fastboot
    CROSS_COMPILE=${TARGET_PREFIX} make PLAT=${UEFIMACHINE} DEBUG=0

    cd ${EDK2_DIR}/l-loader
    ln -s ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin
    ln -s ${EDK2_DIR}/atf-fastboot/build/${UEFIMACHINE}/release/bl1.bin fastboot.bin
    make -f ${UEFIMACHINE}.mk l-loader.bin
    for ptable in linux-4g linux-8g; do
      PTABLE=${ptable} SECTOR_SIZE=512 bash -x generate_ptable.sh
      mv prm_ptable.img ptable-${ptable}.img
    done
}

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin ${D}${libdir}/edk2/bl1.bin

    # Install grub configuration
    sed -e "s|@DISTRO_NAME|${DISTRO_NAME}|" \
        -e "s|@KERNEL_IMAGETYPE|${KERNEL_IMAGETYPE}|" \
        -e "s|@CMDLINE|${CMDLINE}|" \
        < ${WORKDIR}/grub.cfg.in \
        > ${WORKDIR}/grub.cfg
    install -D -p -m0644 ${WORKDIR}/grub.cfg ${D}/boot/grub/grub.cfg
}

# Create a 64M boot image. block size is 1024. (64*1024=65536)
BOOT_IMAGE_SIZE = "65536"
BOOT_IMAGE_BASE_NAME = "boot-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
BOOT_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"

# HiKey boot image requires fastboot and grub EFI
# ensure we deploy grubaa64.efi before we try to create the boot image.
do_deploy[depends] += "grub:do_deploy"
do_deploy_append() {
    cd ${EDK2_DIR}/l-loader
    install -D -p -m0644 l-loader.bin ${DEPLOYDIR}/bootloader/l-loader.bin
    cp -a ptable*.img ${DEPLOYDIR}/bootloader/

    # Ship nvme.img with UEFI binaries for convenience
    dd if=/dev/zero of=${DEPLOYDIR}/bootloader/nvme.img bs=128 count=1024

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${EDK2_DIR}/Build/HiKey/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)
}
