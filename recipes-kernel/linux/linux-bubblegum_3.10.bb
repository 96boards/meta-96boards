require linux.inc

DESCRIPTION = "Generic 96boards kernel"

PV = "3.10.52+git"
SRCREV_kernel = "35be6ad831e4966dc5c6c3ff5a718b1aab6bb81b"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://github.com/96boards-bubblegum/linux.git;protocol=https;branch=bubblegum96-3.10;name=kernel \
           file://0001-compiler-gcc-integrate-the-various-compiler-gcc-345-.patch \
           file://0002-arm64-kill-off-the-libgcc-dependency.patch \
           file://0003-option-serial-remove-duplicate-define.patch \
          "

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "bubblegum"
KERNEL_IMAGETYPE ?= "Image"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

LDFLAGS += "${TOOLCHAIN_OPTIONS}"

do_configure() {
    # Make sure to disable debug info and enable ext4fs built-in
    sed -e '/CONFIG_EXT4_FS=/d' \
        -e '/CONFIG_DEBUG_INFO=/d' \
        < ${S}/arch/arm64/configs/owl_evb_linux_hdmi_defconfig \
        > ${B}/.config

    echo 'CONFIG_EXT4_FS=y' >> ${B}/.config
    echo '# CONFIG_DEBUG_INFO is not set' >> ${B}/.config

    # Disable problematic wifi driver with duplicate symbols
    sed -i -e '/CONFIG_RTL87/d' \
           -e '/CONFIG_R87/d' \
          ${B}/.config

    echo '# CONFIG_RTL8723BU is not set' >> ${B}/.config 
    echo '# CONFIG_RTL8723BS is not set' >> ${B}/.config 

    # Workaround build failure with GCC6:
    # Don't build ARM AMBA Multimedia Card Interface support as a module
    sed -i -e '/CONFIG_MMC_ARMMMCI/d' ${B}/.config
    echo 'CONFIG_MMC_ARMMMCI=y' >> ${B}/.config

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
}
