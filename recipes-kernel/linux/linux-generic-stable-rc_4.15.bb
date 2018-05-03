require linux.inc
require kselftests.inc

DESCRIPTION = "Generic Linux Stable RC 4.15 kernel"

PV = "4.15+git${SRCPV}"
SRCREV_kernel = "d8a5b80568a9cb66810e75b182018e9edb68e8ff"
SRCREV_FORMAT = "kernel"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable-rc.git;protocol=https;branch=linux-4.15.y;name=kernel \
    file://distro-overrides.config;subdir=git/kernel/configs \
    file://systemd.config;subdir=git/kernel/configs \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "hikey|dragonboard-410c|am57xx-evm|intel-core2-32|juno|stih410-b2260"
KERNEL_IMAGETYPE ?= "Image"
KERNEL_CONFIG_FRAGMENTS += "\
    ${S}/kernel/configs/distro-overrides.config \
    ${S}/kernel/configs/systemd.config \
"

# make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"

do_configure() {
    touch ${B}/.scmversion ${S}/.scmversion

    # While kernel.bbclass has an architecture mapping, we can't use it because
    # the kernel config file has a different name.
    case "${HOST_ARCH}" in
      aarch64)
        cp ${S}/arch/arm64/configs/defconfig ${B}/.config
      ;;
      arm)
        cp ${S}/arch/arm/configs/multi_v7_defconfig ${B}/.config
        echo 'CONFIG_ARM_TI_CPUFREQ=y' >> ${B}/.config
        echo 'CONFIG_SERIAL_8250_OMAP=y' >> ${B}/.config
        echo 'CONFIG_POSIX_MQUEUE=y' >> ${B}/.config
      ;;
      x86_64)
        cp ${S}/arch/x86/configs/x86_64_defconfig ${B}/.config
        echo 'CONFIG_IGB=y' >> ${B}/.config
        # FIXME https://bugs.linaro.org/show_bug.cgi?id=3459
        # x86 fails to build:
        # | kernel-source/Makefile:938:
        # *** "Cannot generate ORC metadata for CONFIG_UNWINDER_ORC=y,
        # please install libelf-dev, libelf-devel or elfutils-libelf-devel".  Stop.
        echo 'CONFIG_UNWINDER_FRAME_POINTER=y' >> ${B}/.config
        echo '# CONFIG_UNWINDER_ORC is not set' >> ${B}/.config
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
    cp -a ${B}/vmlinux ${DEPLOYDIR}
    cp ${T}/log.do_compile ${T}/log.do_compile_kernelmodules ${DEPLOYDIR}

    # FIXME 410c fails to build when skales in invoked
    # |   File "/usr/bin/skales/dtbTool", line 239, in __init__
    # |     self.msm_id[0] = soc_ids[matches['soc']] | (foundry << 16)
    # | KeyError: u'ipq8074'
    ( cd ${B}/arch/arm64/boot/dts/qcom/ && rm -vf *ipq8074* *sdm845* ) || true
}

require machine-specific-hooks.inc
