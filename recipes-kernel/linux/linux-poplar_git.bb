require linux.inc

DESCRIPTION = "96boards-poplar kernel"

DEPENDS_append = " dosfstools-native mtools-native u-boot-poplar"

PV = "4.9+git${SRCPV}"
SRCREV = "e153b53cbd7047d7e6863c1850dda751f4a7f333"
SRC_URI = "git://github.com/Linaro/poplar-linux.git;protocol=https;branch=poplar-4.9;name=kernel \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "poplar"
KERNEL_IMAGETYPE ?= "Image"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    # Make sure to disable debug info and enable ext4fs built-in
    sed -e '/CONFIG_EXT4_FS=/d' \
        -e '/CONFIG_DEBUG_INFO=/d' \
        < ${S}/arch/arm64/configs/poplar_defconfig \
        > ${B}/.config

    echo 'CONFIG_EXT4_FS=y' >> ${B}/.config
    echo '# CONFIG_DEBUG_INFO is not set' >> ${B}/.config

    # Check for kernel config fragments. The assumption is that the config
    # fragment will be specified with the absolute path. For example:
    #   * ${WORKDIR}/config1.cfg
    #   * ${S}/config2.cfg
    # Iterate through the list of configs and make sure that you can find
    # each one. If not then error out.
    # NOTE: If you want to override a configuration that is kept in the kernel
    #       with one from the OE meta data then you should make sure that the
    #       OE meta data version (i.e. ${WORKDIR}/config1.cfg) is listed
    #       after the in-kernel configuration fragment.
    # Check if any config fragments are specified.
    if [ ! -z "${KERNEL_CONFIG_FRAGMENTS}" ]; then
        for f in ${KERNEL_CONFIG_FRAGMENTS}; do
            # Check if the config fragment was copied into the WORKDIR from
            # the OE meta data
            if [ ! -e "$f" ]; then
                echo "Could not find kernel config fragment $f"
                exit 1
            fi
        done

        # Now that all the fragments are located merge them.
        ( cd ${WORKDIR} && ${S}/scripts/kconfig/merge_config.sh -m -r -O ${B} ${B}/.config ${KERNEL_CONFIG_FRAGMENTS} 1>&2 )
    fi

    yes '' | oe_runmake -C ${S} O=${B} oldconfig

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

# Create a 128M boot image. block size is 1024. (128*1024=131072)
BOOT_IMAGE_SIZE = "131072"
BOOT_IMAGE_BASE_NAME = "boot-${PKGV}-${PKGR}-${MACHINE}-${DATETIME}"
BOOT_IMAGE_BASE_NAME[vardepsexclude] = "DATETIME"

do_deploy_append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}

    # Create boot image
    mkfs.vfat -F32 -n "boot" -C ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ${BOOT_IMAGE_SIZE}

    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ::extlinux
    mmd -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ::hisilicon
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ${DEPLOYDIR}/${KERNEL_IMAGETYPE} ::${KERNEL_IMAGETYPE}
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ${DEPLOYDIR}/Image-hi3798cv200-poplar.dtb ::hisilicon/hi3798cv200-poplar.dtb
    mcopy -i ${DEPLOYDIR}/${BOOT_IMAGE_BASE_NAME}.img ${DEPLOY_DIR_IMAGE}/extlinux.conf ::extlinux/extlinux.conf

    (cd ${DEPLOYDIR} && ln -sf ${BOOT_IMAGE_BASE_NAME}.img boot-${MACHINE}.img)
}
