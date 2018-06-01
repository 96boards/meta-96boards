do_install_append () {
    # Remove EFI modules if available as that is covered by grub-efi
    if [ "${GRUBPLATFORM}" = "efi" ]; then
        rm -rf ${D}${libdir}
    fi
}
