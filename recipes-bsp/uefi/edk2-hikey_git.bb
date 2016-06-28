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

do_deploy() {
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl1.bin ${DEPLOYDIR}/bl1.bin
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/bl2.bin ${DEPLOYDIR}/bl2.bin
    install -D -p -m0644 ${EDK2_DIR}/atf/build/${UEFIMACHINE}/release/fip.bin ${DEPLOYDIR}/fip.bin
    install -D -p -m0644 ${EDK2_DIR}/Build/HiKey/RELEASE_GCC49/AARCH64/AndroidFastbootApp.efi ${DEPLOYDIR}/fastboot.efi

    # Ship nvme.img with UEFI binaries for convenience
    dd if=/dev/zero of=${DEPLOYDIR}/nvme.img bs=128 count=1024
}
