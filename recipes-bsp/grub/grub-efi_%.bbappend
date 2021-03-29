FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://cfg.emmc \
           file://cfg.sdcard \
           "

GRUB_BUILDIN_append = " chain echo efinet eval font gettext gfxterm gzio help lsefi read regexp search_fs_file search_fs_uuid search_label terminal terminfo tftp time"

python () {
    # Standard config provided by OE-Core
    grub_cfg = 'cfg'

    emmc = d.getVar('CMDLINE_ROOT_EMMC', True)
    sdcard = d.getVar('CMDLINE_ROOT_SDCARD', True)
    cmdline = d.getVar('CMDLINE', True)
    if emmc is not None and emmc in cmdline:
        grub_cfg = 'cfg.emmc'
    elif sdcard is not None and sdcard in cmdline:
        grub_cfg = 'cfg.sdcard'

    d.setVar('GRUB_CFG', grub_cfg)
}

do_mkimage_append_class-target() {
	# Search for the grub.cfg on the local boot media by using the
	# built in cfg file provided via this recipe
	grub-mkimage -c ../${GRUB_CFG} -p /EFI/BOOT -d ./grub-core/ \
	               -O ${GRUB_TARGET}-efi -o ./${GRUB_IMAGE_PREFIX}${GRUB_IMAGE} \
	               ${GRUB_BUILDIN}
}
