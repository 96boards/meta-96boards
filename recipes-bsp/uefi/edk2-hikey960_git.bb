require edk2_git.bb

COMPATIBLE_MACHINE = "hikey960"

DEPENDS_append = " dosfstools-native gptfdisk-native mtools-native virtual/fakeroot-native grub"

inherit deploy pythonnative

SRCREV_edk2 = "65da72b795c3052be21d9369897292bd4f0f0d12"
SRCREV_atf = "cebec7421b1f8bf168239d2ecc75a398aa4072fe"
SRCREV_openplatformpkg = "8c2f9655ec46036ed7e412defe851b99fa205b75"
SRCREV_uefitools = "42eac07beb4da42a182d2a87d6b2e928fc9a31cf"
SRCREV_lloader = "79530f6d9b2668e2d3a76adadcc0053015937e82"
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
    make -f ${UEFIMACHINE}.mk l-loader.bin
    PTABLE=aosp-32g SECTOR_SIZE=4096 SGDISK=./sgdisk bash -x generate_ptable.sh
}

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey960/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
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

# HiKey960 boot image requires fastboot and grub EFI
# ensure we deploy grubaa64.efi before we try to create the boot image.
do_deploy[depends] += "grub:do_deploy"
do_deploy_append() {
    cd ${EDK2_DIR}/l-loader
    install -D -p -m0644 l-loader.bin ${DEPLOYDIR}/bootloader/l-loader.bin
    cp -a prm_ptable.img ${DEPLOYDIR}/bootloader/
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
}
