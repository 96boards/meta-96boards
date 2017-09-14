require edk2_git.bb

COMPATIBLE_MACHINE = "hikey960"

DEPENDS_append = " dosfstools-native mtools-native fakeroot-native grub"

inherit deploy pythonnative

SRCREV_edk2 = "ba458199435ce045086e522c4aec8743e954b329"
SRCREV_atf = "edbd7bb7dcfb0faddb512d6f1808d303631d303a"
SRCREV_openplatformpkg = "32de72337511f38ff7faafa4d3dcd2e80e2f246c"
SRCREV_uefitools = "e960afaa7ce3474724a8548a746b97b4cd0ff500"
SRCREV_lloader = "e720b9b9477e4a2b29c6f018da78dc02db870dbf"
SRCREV_toolsimageshikey960 = "ccb401f726346355e948ec776a411ad037bab4cc"

SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=testing/hikey960_v2.5 \
           git://github.com/ARM-software/arm-trusted-firmware.git;name=atf;branch=integration;destsuffix=git/atf \
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=testing/hikey960_v1.3.4;destsuffix=git/OpenPlatformPkg \
           git://git.linaro.org/uefi/uefi-tools.git;name=uefitools;destsuffix=git/uefi-tools \
           git://github.com/96boards-hikey/l-loader.git;name=lloader;branch=testing/hikey960_v1.2;destsuffix=git/l-loader \
           git://github.com/96boards-hikey/tools-images-hikey960.git;name=toolsimageshikey960;destsuffix=git/tools-images-hikey960 \
           file://grub.cfg.in \
           file://config \
          "

# /usr/lib/edk2/bl1.bin not shipped files. [installed-vs-shipped]
INSANE_SKIP_${PN} += "installed-vs-shipped"

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
}

do_compile_append() {
    cd ${EDK2_DIR}/l-loader
    ln -s ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin
    ln -s ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/fip.bin
    ln -s ${EDK2_DIR}/Build/HiKey960/RELEASE_${AARCH64_TOOLCHAIN}/FV/BL33_AP_UEFI.fd
    PTABLE=aosp-32g SECTOR_SIZE=4096 SGDISK=./sgdisk bash -x generate_ptable.sh
    python gen_loader_hikey960.py -o l-loader.bin --img_bl1=bl1.bin --img_ns_bl1u=BL33_AP_UEFI.fd
}

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey960/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin ${D}${libdir}/edk2/bl1.bin

    # Install grub configuration
    sed -e "s|@DISTRO|${DISTRO}|" \
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

# HiKey960 boot image requires fastboot and grub EFI
# ensure we deploy grubaa64.efi before we try to create the boot image.
do_deploy[depends] += "grub:do_deploy"
do_deploy_append() {
    mkdir -p ${DEPLOYDIR}/bootloader

    cd ${EDK2_DIR}/l-loader
    cp -a l-loader.bin prm_ptable.img ${DEPLOYDIR}/bootloader/
    cd ${EDK2_DIR}/tools-images-hikey960
    cp -a hikey_idt sec_uce_boot.img sec_usb_xloader.img sec_xloader.img ${DEPLOYDIR}/bootloader/
    cp -a ${WORKDIR}/config ${DEPLOYDIR}/bootloader/

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${EDK2_DIR}/Build/HiKey960/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)

    # Fix up - move bootloader related files into a subdir
    mv ${DEPLOYDIR}/fip.bin ${DEPLOYDIR}/bootloader/
    rm -f ${DEPLOY_DIR_IMAGE}/grubaa64.efi
}
