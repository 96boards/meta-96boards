DEPENDS += "dosfstools-native mtools-native"

# Create a 64M boot image. block size is 1024. (64*1024=65536)
BOOT_IMAGE_SIZE = "65536"
BOOT_IMAGE_BASE_NAME = "boot-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
BOOT_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy_append_hikey() {
    # FIXME: build grubaa64.efi instead of pre-built file
    GRUB_EFI_BUILD_NUMBER=$(wget -q --no-check-certificate -O - https://ci.linaro.org/job/96boards-reference-grub-efi-arm64/lastSuccessfulBuild/buildNumber)
    GRUB_EFI_URL="https://builds.96boards.org/snapshots/reference-platform/components/grub/${GRUB_EFI_BUILD_NUMBER}"
    wget --progress=dot ${GRUB_EFI_URL}/grubaa64.efi -O ${DEPLOY_DIR_IMAGE}/grubaa64.efi

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/fastboot.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img
    rm -f ${DEPLOY_DIR_IMAGE}/*.efi

    (cd ${DEPLOY_DIR_IMAGE} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)
}
