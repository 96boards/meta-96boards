require linux.inc
require kselftests.inc

DESCRIPTION = "Generic Linux mainline kernel"

PV = "4.12+git${SRCPV}"
SRCREV_kernel = "41f1830f5a7af77cf5c86359aba3cbd706687e52"
SRCREV_FORMAT = "kernel"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git;protocol=https;branch=master;name=kernel \
    file://distro-overrides.config;subdir=git/kernel/configs \
    file://systemd.config;subdir=git/kernel/configs \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey|dragonboard-410c|am57xx-evm|intel-corei7-64"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_CONFIG_FRAGMENTS += "\
    ${S}/kernel/configs/distro-overrides.config \
    ${S}/kernel/configs/systemd.config \
"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    # While kernel.bbclass has an architecture mapping, we can't use it because
    # the kernel config file has a different name.
    case "${HOST_ARCH}" in
      aarch64)
        cp ${S}/arch/arm64/configs/defconfig ${B}/.config
      ;;
      arm)
        cp ${S}/arch/arm/configs/multi_v7_defconfig ${B}/.config
      ;;
      x86_64)
        cp ${S}/arch/x86/configs/x86_64_defconfig ${B}/.config
      ;;
    esac

    # Make sure to enable NUMA
    echo 'CONFIG_NUMA=y' >> ${B}/.config

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

    oe_runmake -C ${S} O=${B} kselftest-merge

    bbplain "Saving defconfig to:\n${B}/defconfig"
    oe_runmake -C ${B} savedefconfig
}

do_deploy_append() {
    cp -a ${B}/defconfig ${DEPLOYDIR}
    cp -a ${B}/.config ${DEPLOYDIR}/config
}

require machine-specific-hooks.inc
