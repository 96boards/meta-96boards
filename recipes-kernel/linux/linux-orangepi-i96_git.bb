require linux.inc

DESCRIPTION = "OrangePi i96 vendor kernel"

PV = "3.10.62+git${SRCPV}"
SRCREV_kernel = "b476a2c5bfb893aba3ee4daa840196a97898bf48"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://github.com/daniel-thompson/linux.git;protocol=https;name=kernel;nobranch=1"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "orangepi-i96"
KERNEL_IMAGETYPE ?= "zImage"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    cp ${S}/arch/arm/configs/orangepi_i96_defconfig ${B}/.config

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

    oe_runmake -C ${S} O=${B} olddefconfig

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

do_deploy:append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}
}
