require edk2_git.bb

COMPATIBLE_MACHINE = "hikey"

DEPENDS_append = " dosfstools-native mtools-native grub optee-os"

SRCREV_edk2 = "06e4def583a56aebb67d11ab8f782220bbc5f621"
SRCREV_atf = "4adfdd06f11deb2ab6a056a68ed6f22dcb99a791"
SRCREV_openplatformpkg = "f70886cd45a12a0ce961752de55dc70a878f8a15"

SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=hikey-aosp \
           git://github.com/96boards-hikey/arm-trusted-firmware.git;name=atf;branch=hikey;destsuffix=git/atf \
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=hikey-aosp;destsuffix=git/OpenPlatformPkg \
           file://grub.cfg.in \
          "

# /usr/lib/edk2/bl1.bin not shipped files. [installed-vs-shipped]
INSANE_SKIP_${PN} += "installed-vs-shipped"

OPTEE_OS_ARG = "-s ${EDK2_DIR}/optee_os"

# We need the secure payload (Trusted OS) built from OP-TEE Trusted OS (tee.bin)
# but we have already built tee.bin from optee-os recipe and
# uefi-build.sh script has a few assumptions...
# Copy tee.bin and create dummy files to make uefi-build.sh script happy
do_compile_prepend() {
    install -D -p -m0644 \
      ${STAGING_DIR_HOST}/lib/firmware/tee.bin \
      ${EDK2_DIR}/optee_os/out/arm-plat-hikey/core/tee.bin

    mkdir -p ${EDK2_DIR}/optee_os/documentation
    touch ${EDK2_DIR}/optee_os/documentation/optee_design.md

    printf "all:\n"  > ${EDK2_DIR}/optee_os/Makefile
    printf "\ttrue" >> ${EDK2_DIR}/optee_os/Makefile
}

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
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

# HiKey boot image requires fastboot and grub EFI
# ensure we deploy grubaa64.efi before we try to create the boot image.
do_deploy[depends] += "grub:do_deploy"
do_deploy_append() {
    # Ship nvme.img with UEFI binaries for convenience
    dd if=/dev/zero of=${DEPLOYDIR}/nvme.img bs=128 count=1024

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${EDK2_DIR}/Build/HiKey/RELEASE_${AARCH64_TOOLCHAIN}/AARCH64/AndroidFastbootApp.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)
}
