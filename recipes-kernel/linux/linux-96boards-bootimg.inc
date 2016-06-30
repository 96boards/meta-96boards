DEPENDS += "dosfstools-native mtools-native"

DEPENDS_append_hikey = " grub edk2-hikey"

# Create a 64M boot image. block size is 1024. (64*1024=65536)
BOOT_IMAGE_SIZE = "65536"
BOOT_IMAGE_BASE_NAME = "boot-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
BOOT_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy_append_hikey() {
    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${BOOT_IMAGE_SIZE}
    mmd -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI
    mmd -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ::EFI/BOOT
    mcopy -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/fastboot.efi ::EFI/BOOT/fastboot.efi
    mcopy -i ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img ${DEPLOY_DIR_IMAGE}/grubaa64.efi ::EFI/BOOT/grubaa64.efi
    chmod 644 ${DEPLOY_DIR_IMAGE}/${BOOT_IMAGE_BASE_NAME}.uefi.img

    (cd ${DEPLOY_DIR_IMAGE} && ln -sf ${BOOT_IMAGE_BASE_NAME}.uefi.img boot-${MACHINE}.uefi.img)
}
