require linux.inc

DESCRIPTION = "ARM LT kernel"

PV = "4.9.0+git"
SRCREV_kernel = "bd4ec9ac3787a05c0e6878e09aa8d2b66dd3f80d"
SRCREV_FORMAT = "kernel"

SRC_URI = "git://git.linaro.org/landing-teams/working/arm/kernel-release.git;protocol=https;branch=latest-armlt;name=kernel \
          "
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "juno"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_CONFIG_FRAGMENTS_juno += " \
       ${S}/linaro/configs/linaro-base.conf \
       ${S}/linaro/configs/linaro-base-arm64.conf \
       ${S}/linaro/configs/distribution.conf \
       ${S}/linaro/configs/vexpress64.conf \
      "

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    touch ${B}/.config

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

    # Make sure to disable debug info and enable ext4fs built-in
    echo 'CONFIG_EXT4_FS=y' >> ${B}/.config
    echo '# CONFIG_DEBUG_INFO is not set' >> ${B}/.config

    yes '' | oe_runmake -C ${S} O=${B} oldconfig
}
