require edk2_git.bb

COMPATIBLE_MACHINE = "hikey970"

DEPENDS_append = " dosfstools-native gptfdisk-native mtools-native virtual/fakeroot-native grub"

inherit deploy pythonnative

SRCREV_edk2="307624caaf3131b671868c5b326730f4c8be4ffb"
SRCREV_atf = "53f93e5c738c37378811cfc08ef0ccb8626e2d93"
SRCREV_openplatformpkg = "348ccb5022f452273236e36ebe4238443ba256f8"
SRCREV_uefitools = "fd431116e6e86677837d026f99279bf5b72347d1"
SRCREV_lloader = "9bc7a3e03d2af634cd9ef2fba28434609a259dc8"
SRCREV_toolsimageshikey970 = "7ea233bb060de5d8947a0019c3a45f20a4cfdb62"
SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=hikey970_v1.0;destsuffix=git  \
	   git://github.com/96boards-hikey/arm-trusted-firmware.git;name=atf;branch=hikey970_v1.0;destsuffix=git/atf \ 
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=hikey970_v1.0;destsuffix=git/OpenPlatformPkg \
	   git://github.com/Mani-Sadhasivam/uefi-tools.git;name=uefitools;branch=hikey970_v1.0;destsuffix=git/uefi-tools \
	   git://github.com/96boards-hikey/l-loader.git;name=lloader;branch=hikey970_v1.0;destsuffix=git/l-loader \
           git://github.com/96boards-hikey/tools-images-hikey970.git;name=toolsimageshikey970;branch=hikey970_v1.0;destsuffix=git/tools-images-hikey970 \
           file://grub.cfg.in \
           file://config \
           file://atf-build-sh_fix_path_to_BL32_images.patch;patchdir=uefi-tools \
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
    ln -s ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/FV/bl1.bin
    ln -s ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/FV/bl2.bin
    ln -s ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/FV/fip.bin
    ln -s ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/FV/BL33_AP_UEFI.fd
    make -f ${UEFIMACHINE}.mk 
    make -f ${UEFIMACHINE}.mk l-loader.bin
    PTABLE=aosp-32g SECTOR_SIZE=4096 SGDISK=./sgdisk bash -x generate_ptable.sh
}

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/FV/bl1.bin ${D}${libdir}/edk2/bl1.bin

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

# HiKey970 boot image requires fastboot and grub EFI
# ensure we deploy grubaa64.efi before we try to create the boot image.
do_deploy[depends] += "grub:do_deploy"
do_deploy_append() {
    cd ${EDK2_DIR}/l-loader
    install -D -p -m0644 l-loader.bin ${DEPLOYDIR}/bootloader/l-loader.bin
    cp -a prm_ptable.img ${DEPLOYDIR}/bootloader/
    cd ${EDK2_DIR}/tools-images-hikey970
    cp -a hikey_idt sec_usb_xloader.img sec_usb_xloader2.img sec_xloader.img ${DEPLOYDIR}/bootloader/
    cp -a ${WORKDIR}/config ${DEPLOYDIR}/bootloader/

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${EDK2_DIR}/Build/HiKey970/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)

    # Fix up - move bootloader related files into a subdir
    mv ${DEPLOYDIR}/fip.bin ${DEPLOYDIR}/bootloader/
    rm -f ${DEPLOY_DIR_IMAGE}/grubaa64.efi
}
