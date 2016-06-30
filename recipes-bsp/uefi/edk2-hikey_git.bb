require edk2_git.bb

COMPATIBLE_MACHINE = "hikey"

SRCREV_edk2 = "76c7cfcc22c7448638acb6f904088b2ff3f79f63"
SRCREV_atf = "bdec62eeb8f3153a4647770e08aafd56a0bcd42b"
SRCREV_openplatformpkg = "db64042266ed377f4a6748232497de8e05d36e35"

SRC_URI = "git://github.com/96boards-hikey/edk2.git;name=edk2;branch=hikey-aosp \
           git://github.com/96boards-hikey/arm-trusted-firmware.git;name=atf;branch=hikey;destsuffix=git/atf \
           git://github.com/96boards-hikey/OpenPlatformPkg.git;name=openplatformpkg;branch=hikey-aosp;destsuffix=git/OpenPlatformPkg \
          "

do_install() {
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey/RELEASE_GCC49/AARCH64/AndroidFastbootApp.efi ${D}/boot/EFI/BOOT/fastboot.efi
}

# FIXME: HiKey boot image requires fastboot and grub EFI
# ensure we deploy fastboot.efi before we try to create the boot image.
# ideally, we create the boot image in edk2-hikey and depends on grub
# as the HiKey boot image doesn't contain any kernel artifacts
do_deploy[depends] += "virtual/kernel:do_shared_workdir"
do_deploy() {
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin ${DEPLOY_DIR_IMAGE}/bl1.bin
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl2.bin ${DEPLOY_DIR_IMAGE}/bl2.bin
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/fip.bin ${DEPLOY_DIR_IMAGE}/fip.bin
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey/RELEASE_GCC49/AARCH64/AndroidFastbootApp.efi ${DEPLOY_DIR_IMAGE}/fastboot.efi

    # Ship nvme.img with UEFI binaries for convenience
    dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/nvme.img bs=128 count=1024
}
