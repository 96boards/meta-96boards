DEPENDS += "dosfstools-native mtools-native"

# Create a 64M boot image. block size is 1024. (64*1024=65536)
BOOT_IMAGE_SIZE = "65536"
BOOT_IMAGE_BASE_NAME = "boot-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
BOOT_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy_append_hikey() {
    # FIXME: build grubaa64.efi instead of pre-built file
    GRUB_EFI_BUILD_NUMBER=$(wget -q --no-check-certificate -O - https://ci.linaro.org/job/96boards-reference-grub-efi-arm64/lastSuccessfulBuild/buildNumber)
    GRUB_EFI_URL="https://builds.96boards.org/snapshots/reference-platform/components/grub/${GRUB_EFI_BUILD_NUMBER}"
    wget --progress=dot ${GRUB_EFI_URL}/grubaa64.efi -O ${DEPLOYDIR}/grubaa64.efi

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOYDIR}/fastboot.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOYDIR}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.uefi.img
    rm -f ${DEPLOYDIR}/*.efi

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)
}
