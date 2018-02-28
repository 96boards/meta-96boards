require linux.inc
require kselftests.inc

DESCRIPTION = "Generic Linux Stable RC 4.9 kernel"

PV = "4.9+git${SRCPV}"
SRCREV_kernel = "2e0b83086b5756a4c7f1fd04636a20b8e15dadd4"
SRCREV_FORMAT = "kernel"

SRC_URI = "\
    git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux-stable-rc.git;protocol=https;branch=linux-4.9.y;name=kernel \
    file://distro-overrides.config;subdir=git/kernel/configs \
    file://systemd.config;subdir=git/kernel/configs \
    file://0001-selftests-lib-add-config-fragment-for-bitmap-printf-.patch \
    file://0001-selftests-ftrace-add-CONFIG_KPROBES-y-to-the-config-.patch \
    file://0001-selftests-vm-add-CONFIG_SYSVIPC-y-to-the-config-frag.patch \
    file://0001-selftests-gpio-add-config-fragment-for-gpio-mockup.patch \
    file://0001-selftests-create-cpufreq-kconfig-fragments.patch \
    file://0001-selftests-sync-add-config-fragment-for-testing-sync-.patch \
    file://0001-selftests-ftrace-add-more-config-fragments.patch \
"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "am57xx-evm|dragonboard-410c|hikey|intel-core2-32|juno|stih410-b2260"
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
        echo 'CONFIG_STUB_CLK_HI6220=y' >> ${B}/.config
      ;;
      arm)
        cp ${S}/arch/arm/configs/multi_v7_defconfig ${B}/.config
        echo 'CONFIG_SERIAL_8250_OMAP=y' >> ${B}/.config
        echo 'CONFIG_POSIX_MQUEUE=y' >> ${B}/.config
      ;;
      x86_64)
        cp ${S}/arch/x86/configs/x86_64_defconfig ${B}/.config
        echo 'CONFIG_IGB=y' >> ${B}/.config
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
}

require machine-specific-hooks.inc
