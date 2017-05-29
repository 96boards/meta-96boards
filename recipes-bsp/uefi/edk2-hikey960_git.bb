require edk2_git.bb

COMPATIBLE_MACHINE = "hikey960"

DEPENDS_append = " dosfstools-native mtools-native fakeroot-native grub"

inherit deploy pythonnative

SRCREV_edk2 = "7efa39f3631ca8a42cc17210fb293b850aecbf3f"
SRCREV_atf = "58c95367b955b8c1f29962c72dd4e5ea23c74776"
SRCREV_openplatformpkg = "5960fa698ccb61a7a131b8be18c37a355044d88b"
SRCREV_uefitools = "9857704d962ed9dad02e7cee2f3de240d132c872"
SRCREV_lloader = "2835202b47bf734e48c6c25cd35ffbeecb8aa141"
SRCREV_toolsimageshikey960 = "5716ead2fbf063b1ff578de7d6004a100d2ff92d"

SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=testing/hikey960_v2.5 \
           git://github.com/96boards-hikey/arm-trusted-firmware.git;name=atf;branch=testing/hikey960_v1.1;destsuffix=git/atf \
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=testing/hikey960_v1.3.4;destsuffix=git/OpenPlatformPkg \
           git://github.com/96boards-hikey/uefi-tools.git;name=uefitools;branch=testing/hikey960_v1;destsuffix=git/uefi-tools \
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
